from fastapi import FastAPI
from app.api.v1 import router as v1_router

app = FastAPI(title="AURA VPN API", version="0.1.0")
app.include_router(v1_router, prefix="/v1")

@app.get("/health")
def health():
    return {"ok": True}
