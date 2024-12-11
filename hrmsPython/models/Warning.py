from dataclasses import dataclass
from datetime import datetime
from enum import Enum
from typing import Optional

from dataclasses_json import dataclass_json

from models import Contractor


class WarningReason(Enum):
    LATE = "LATE"
    ABSENT = "ABSENT"
    MISCONDUCT = "MISCONDUCT"


class WarningState(Enum):
    APPEALED = "APPEALED"
    ACTIVE = "ACTIVE"
    REMOVED = "REMOVED"
    FINAL = "FINAL"

@dataclass_json
@dataclass
class Warnings:
    warningId: Optional[int] = None
    dateIssue: Optional[datetime] = None
    reason: Optional[WarningReason] = None
    state: Optional[WarningState] = None
    contractor: Optional[Contractor] = None
