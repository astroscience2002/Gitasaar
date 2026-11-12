#!/usr/bin/env python3
"""Retrieve three public-domain paintings for bundled Android drawable-nodpi.
Sources and public-domain status are documented in THIRD_PARTY_LICENSES.md.
Build fails rather than silently shipping blurred thumbnails.
"""
from pathlib import Path
from urllib.parse import quote
from urllib.request import Request, urlopen
import hashlib
import time

DEST=Path("app/src/main/res/drawable-nodpi")
IMAGES={
    "krishna_chariot.jpg":"Arjuna and His Charioteer Krishna Confront Karna.jpg",
    "krishna_teaching.jpg":"Krishna Arjuna Gita.jpg",
    "krishna_battle.jpg":"Krishna instructing Arjuna.jpg"
}
def download(filename):
    encoded=quote(filename.replace(" ","_"),safe="()_.,")
    digest=hashlib.md5(filename.replace(" ","_").encode("utf-8")).hexdigest()
    return [
        "https://upload.wikimedia.org/wikipedia/commons/"+digest[0]+"/"+digest[:2]+"/"+encoded,
        "https://commons.wikimedia.org/wiki/Special:Redirect/file/"+encoded
    ]

DEST.mkdir(exist_ok=True,parents=True)
for target,source in IMAGES.items():
    output=DEST/target
    if output.exists() and output.stat().st_size>100000:
        continue
    error=None
    for url in download(source):
        for attempt in range(2):
            try:
                headers={"User-Agent":"GitaSaarAndroid/1.0 (open-source educational app; https://github.com/astroscience2002/Gitasaar)"}
                with urlopen(Request(url,headers=headers),timeout=50) as response:
                    data=response.read(12000000)
                if not data.startswith(bytes([0xff,0xd8])) or len(data)<100000:
                    raise ValueError("Not a high resolution JPEG: "+str(len(data)))
                output.write_bytes(data)
                print(target,len(data),"bytes",flush=True)
                break
            except Exception as e:
                error=e
                print("Retrying",target,type(e).__name__,str(e)[:150],flush=True)
                time.sleep(2)
        if output.exists() and output.stat().st_size>100000:
            break
    if not output.exists() or output.stat().st_size<100000:
        raise RuntimeError("Cannot bundle "+target+": "+str(error))
