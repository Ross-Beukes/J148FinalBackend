from dataclasses import dataclass
from typing import Optional
from enum import Enum

from dataclasses_json import dataclass_json

from models import User, ContractPeriod


class Status(Enum):
    EXTERNAL = "EXTERNAL"
    SUSPENDED = "SUSPENDED"
    ACTIVE = "ACTIVE"
    ON_LEAVE = "ON_LEAVE"

@dataclass_json
@dataclass
class Contractor:
    contractorId: Optional[int] = None  # Maps to Long in Java
    status: Optional[Status] = None     # Maps to Status enum in Java
    user: Optional[User] = None         # Maps to User class in Java
    contractPeriod: Optional[ContractPeriod] = None  # Maps to ContractPeriod class in Java
