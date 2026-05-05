# ──────────────────────────────────────────────────────────────────────────────
# Stage 1: Build dependencies
# ──────────────────────────────────────────────────────────────────────────────
FROM python:3.11-slim AS builder

WORKDIR /build

# Install build essentials for compiled packages
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    libgl1-mesa-glx \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir --prefix=/install -r requirements.txt

# ──────────────────────────────────────────────────────────────────────────────
# Stage 2: Production image
# ──────────────────────────────────────────────────────────────────────────────
FROM python:3.11-slim AS production

LABEL maintainer="PDD Medical AI Platform"
LABEL version="1.0.0"

# Runtime dependencies for OpenCV
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgl1-mesa-glx \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Copy installed packages from builder
COPY --from=builder /install /usr/local

WORKDIR /app

# Non-root user for security
RUN useradd -m -u 1000 appuser

# Copy application code
COPY app/ ./app/
COPY scripts/ ./scripts/

# Create required directories
RUN mkdir -p /tmp/pdd_processing /app/checkpoints && \
    chown -R appuser:appuser /app /tmp/pdd_processing

USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD python -c "import urllib.request; urllib.request.urlopen('http://localhost:8000/health')"

EXPOSE 8000

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000", "--workers", "1"]
