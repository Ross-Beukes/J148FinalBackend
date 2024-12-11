from dataclasses import dataclass
from datetime import datetime
from enum import Enum
from typing import Optional

from dataclasses_json import dataclass_json

from models import Contractor  # Assuming Contractor is in the 'models' module


class Outcome(Enum):
    NULL = "NULL"
    SUSPENDED = "SUSPENDED"
    CLEARED = "CLEARED"

@dataclass_json
@dataclass
class Hearing:
    hearingsId: Optional[int] = None
    scheduleDate: Optional[datetime] = None
    outcome: Optional[Outcome] = None
    reason: Optional[str] = None
    contractor: Optional[Contractor] = None
