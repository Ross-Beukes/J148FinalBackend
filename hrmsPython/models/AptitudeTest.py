from dataclasses import dataclass
from dataclasses_json import dataclass_json
from datetime import datetime
from typing import Optional

from models import User

@dataclass_json
@dataclass
class AptitudeTest:
    aptitudeTestId: Optional[int] = None
    testMark: Optional[int] = None
    testDate: Optional[datetime] = None
    user: User = Optional[None]
