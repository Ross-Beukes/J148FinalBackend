from dataclasses import dataclass
from datetime import datetime
from enum import Enum
from typing import Optional

from dataclasses_json import dataclass_json

from models import Contractor


class Register(Enum):
    PRESENT = "PRESENT"
    ABSENT = "ABSENT"
    LATE = "LATE"

@dataclass_json
@dataclass
class Attendance:
    attendanceId: Optional[int] = None
    timeIn: Optional[datetime] = None
    timeOut: Optional[datetime] = None
    register: Optional[Register] = None
    contractor: Optional[Contractor] = None
    contractor: Contractor = None
