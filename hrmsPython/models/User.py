from dataclasses import dataclass
from enum import Enum
from typing import Optional

from dataclasses_json import dataclass_json


class Role(Enum):
    APPLICANT = "PENDING"
    CONTRACTOR = "CONTRACTOR"
    INSTRUCTOR = "INSTRUCTOR"
    ADMIN = "ADMIN"


@dataclass_json
@dataclass
class User:
    user_id: Optional[int] = None
    name: Optional[str] = None
    surname: Optional[str] = None
    email: Optional[str] = None
    password: Optional[str] = None
    gender: Optional[str] = None
    idNumber: Optional[str] = None
    role: Optional[Role] = None
    race: Optional[str] = None
    location: Optional[str] = None
    age: Optional[int] = None
