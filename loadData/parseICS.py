import pandas as pd
import requests
import jsonpickle
from icalendar import Calendar, Event
from enum import Enum

roomDF = pd.read_pickle("./rooms.pkl")


class Week(Enum):
    SU = 0
    MO = 1
    TU = 2
    WE = 3
    TH = 4
    FR = 5
    SA = 6


# class definitions
class customTime:
    def __init__(self, day, hour):
        self.day = day
        self.hour = hour


class interval:
    def __init__(self, start, finish):
        self.start = start
        self.finish = finish


class emptyIntervals:
    def __init__(self):
        self.emptyIntervals = []

    def addInterval(self, interval):
        self.emptyIntervals.append(interval)


class buildings:
    def __init__(self):
        self.buildings = []

    def addBuilding(self, building):
        self.buildings.append(building)


class building:
    def __init__(self, buildingNumber):
        self.buildingNumber = buildingNumber
        self.rooms = []

    def addRoom(self, room):
        self.rooms.append(room)


class room:
    def __init__(self, roomNumber, roomCapacity):
        self.emptyIntervals = []
        self.reservations = {}
        self.reports = {}
        self.roomNumber = roomNumber
        self.roomCapacity = roomCapacity

    def setEmptyIntervals(self, emptyIntervals):
        self.emptyIntervals = emptyIntervals.emptyIntervals


# build for 1 ics file
tempBuildingName = ""
buildingDB = buildings()
tempBuilding = building(roomDF.iloc[0].Building)
for index, row in roomDF.iterrows():
    buildingNumber = row.Building
    if buildingNumber != tempBuilding.buildingNumber:
        # if the building number doesn't match, add the previous tempBuilding to the DB
        # and reinitialize the tempBuilding
        buildingDB.addBuilding(tempBuilding)
        tempBuilding = building(buildingNumber)
    url = "http://schedules.calpoly.edu" + row.ICS
    cal = Calendar.from_ical(requests.get(url).text)
    capacity = row["Loc Cap Reg"]
    # create temp room
    tempRoom = room(row.Room, capacity)
    tempEmptyIntervals = emptyIntervals()
    # populate empty intervals for tempRoom
    for component in cal.walk():
        if component.name == "VEVENT":
            # create empty interval
            dstart = component.get("dtstart").dt
            dtend = component.get("dtend").dt
            rrule = component.get("rrule")
            days = rrule.get("BYDAY")
            for day in days:
                tempIntervalStart = customTime(Week[day].value, dstart.time().hour)
                tempIntervalEnd = customTime(Week[day].value, dtend.time().hour)
                tempInterval = interval(tempIntervalStart, tempIntervalEnd)
                tempEmptyIntervals.addInterval(tempInterval)
    tempRoom.setEmptyIntervals(tempEmptyIntervals)
    tempBuilding.addRoom(tempRoom)
frozen = jsonpickle.encode(buildingDB, unpicklable=False)
with open("polyrooms.json", "w") as f:
    f.write(frozen)