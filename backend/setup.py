"""Package setup for the PDD backend."""

from setuptools import find_packages, setup

setup(
    name="pdd-backend",
    version="1.0.0",
    description="PDD Medical AI Platform Backend",
    packages=find_packages(exclude=["tests*", "scripts*"]),
    python_requires=">=3.11",
    install_requires=[
        "fastapi>=0.111.0",
        "pydantic>=2.7.0",
        "pydantic-settings>=2.2.0",
        "firebase-admin>=6.5.0",
    ],
)
