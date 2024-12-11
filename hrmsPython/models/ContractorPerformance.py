from dataclasses import dataclass
from typing import List, Optional

from dataclasses_json import dataclass_json

from models import Warning, Attendance, Hearing, Contractor, ContractPeriod, User, AptitudeTest

@dataclass_json
@dataclass
class ContractorPerformance:
    warningList: Optional[List[Warning]] = None   # List of Warning objects
    attendanceList: Optional[List[Attendance]] = None  # List of Attendance objects
    hearingList: Optional[List[Hearing]] = None  # List of Hearing objects
    contractor: Optional[Contractor] = None  # Singular Contractor object
    contractPeriod: Optional[ContractPeriod] = None  # Singular ContractPeriod object
    user: Optional[User] = None  # Singular User object
    aptitudeTest: Optional[AptitudeTest] = None  # AptitudeTest object (Optional)
