"""Renders a block model to an inventory-style icon, the way the game shows the block item (gui transform 30/225/0).

The Item Brewing Station has no flat item texture, so its wiki icon is rendered from the block model.
Re-run when the model or texture changes, then upload the 1024px render to the texture bucket:

  py scripts/render-block-icon.py <model.json> <texture.png> <out.png> [size]

Needs Pillow and numpy.
"""
import json, math, sys
import numpy as np
from PIL import Image

model_path, tex_path, out_path = sys.argv[1:4]
SIZE = int(sys.argv[4]) if len(sys.argv) > 4 else 1024
SS = 2  # supersample

model = json.load(open(model_path))
tex = np.asarray(Image.open(tex_path).convert("RGBA")).astype(np.float32)
TH, TW = tex.shape[:2]


def rot(p, axis, deg, origin):
    a = math.radians(deg)
    c, s = math.cos(a), math.sin(a)
    x, y, z = p[0] - origin[0], p[1] - origin[1], p[2] - origin[2]
    if axis == "y":
        x, z = x * c + z * s, -x * s + z * c
    elif axis == "x":
        y, z = y * c - z * s, y * s + z * c
    else:
        x, y = x * c - y * s, x * s + y * c
    return [x + origin[0], y + origin[1], z + origin[2]]


def element_rotate(p, r):
    if not r or r.get("angle", 0) == 0:
        return p
    axis, ang, o = r["axis"], r["angle"], r["origin"]
    q = list(p)
    if r.get("rescale"):
        f = 1 / math.cos(math.radians(abs(ang)))
        for i, ax in enumerate("xyz"):
            if ax != axis:
                q[i] = o[i] + (q[i] - o[i]) * f
    return rot(q, axis, ang, o)


def gui(p):
    # item display: rotation [30, 225, 0] applied Z, then Y, then X; scale 0.625, centred on the block
    q = [p[0] - 8, p[1] - 8, p[2] - 8]
    q = rot(q, "y", 225, [0, 0, 0])
    q = rot(q, "x", 30, [0, 0, 0])
    return [v * 0.625 for v in q]


def corners(face, f, t):
    x1, y1, z1 = f
    x2, y2, z2 = t
    return {
        "north": ([x2, y2, z1], [x1, y2, z1], [x2, y1, z1]),
        "south": ([x1, y2, z2], [x2, y2, z2], [x1, y1, z2]),
        "east": ([x2, y2, z2], [x2, y2, z1], [x2, y1, z2]),
        "west": ([x1, y2, z1], [x1, y2, z2], [x1, y1, z1]),
        "up": ([x1, y2, z1], [x2, y2, z1], [x1, y2, z2]),
        "down": ([x1, y1, z2], [x2, y1, z2], [x1, y1, z1]),
    }[face]


N = SIZE * SS
color = np.zeros((N, N, 4), np.float32)
depth = np.full((N, N), -1e9, np.float32)
# screen: 16 units of scaled model span the whole image
scale = N / 16.0
ys, xs = np.mgrid[0:N, 0:N].astype(np.float32)
sx = (xs + 0.5) / scale - 8
sy = 8 - (ys + 0.5) / scale

for el in model["elements"]:
    for face, fd in el.get("faces", {}).items():
        tl, tr, bl = (gui(element_rotate(p, el.get("rotation"))) for p in corners(face, el["from"], el["to"]))
        tl, tr, bl = map(np.array, (tl, tr, bl))
        e1, e2 = tr - tl, bl - tl
        det = e1[0] * e2[1] - e1[1] * e2[0]
        if abs(det) < 1e-9:
            continue
        # solve (sx,sy) = tl + s*e1 + t*e2
        dx, dy = sx - tl[0], sy - tl[1]
        s = (dx * e2[1] - dy * e2[0]) / det
        t = (e1[0] * dy - e1[1] * dx) / det
        mask = (s >= 0) & (s <= 1) & (t >= 0) & (t <= 1)
        if not mask.any():
            continue
        z = tl[2] + s * e1[2] + t * e2[2]
        u1, v1, u2, v2 = fd.get("uv", [0, 0, 16, 16])
        r = fd.get("rotation", 0)
        ss_, tt_ = s, t
        for _ in range(r // 90):  # rotate uv clockwise
            ss_, tt_ = tt_, 1 - ss_
        u = u1 + (u2 - u1) * ss_
        v = v1 + (v2 - v1) * tt_
        px = np.clip((u / 16 * TW).astype(int), 0, TW - 1)
        py = np.clip((v / 16 * TH).astype(int), 0, TH - 1)
        sample = tex[py, px]
        normal = np.cross(e1, e2)
        normal = normal / np.linalg.norm(normal)
        if normal[2] < 0:
            normal = -normal  # double sided: face the viewer
        shade = np.clip(0.62 + 0.38 * normal[1] - 0.25 * normal[0], 0.45, 1.0)
        m = mask & (sample[..., 3] > 0) & (z > depth)
        depth[m] = z[m]
        color[m, :3] = sample[m, :3] * shade
        color[m, 3] = 255

img = Image.fromarray(color.clip(0, 255).astype(np.uint8), "RGBA")
img = img.resize((SIZE, SIZE), Image.LANCZOS)
img.save(out_path)
print("saved", out_path)
