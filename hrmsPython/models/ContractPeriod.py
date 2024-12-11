from dataclasses import dataclass
from datetime import date
from typing import Optional

from dataclasses_json import dataclass_json


@dataclass_json
@dataclass
class ContractPeriod:
    contractPeriodId: Optional[int] = None
    name: Optional[str] = None
    startDate: Optional[date] = None
    endDate: Optional[date] = None
