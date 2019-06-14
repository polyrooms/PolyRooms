import lxml.html as LH
import pandas as pd
import requests
import re

url = "http://schedules.calpoly.edu/all_location_curr.htm"
page = requests.get(url)
content = page.content

# remove rows with nonsense data
rowsSkip = [0]
df = pd.read_html(content, header=0, skiprows=rowsSkip, keep_default_na=False)[0]
lhTable = LH.fromstring(content)

for index, row in df.iterrows():
    if re.match(".*-.*", row["Listing"]) is None:
        rowsSkip.append(index + 2)

df = pd.read_html(page.content, header=0, skiprows=rowsSkip, keep_default_na=False)[
    0
]  # assign ICS href to ICS columns
icsHref = lhTable.xpath("//tr/td//a/@href")
icsRegex = re.compile(r"\/ics\/location.*")
icsHref = list(filter(icsRegex.match, icsHref))
df["ICS"] = icsHref  # get building names
buildingNames = []
roomNames = []
buildingRegex = re.compile(r"(.+?)[-\s].*")
roomRegex = re.compile(r".*[-](\w+)(.*)")
for index, row in df.iterrows():
    text = row.Listing
    buildingFound = buildingRegex.search(text)
    roomFound = roomRegex.search(text)
    if buildingFound:
        building = buildingFound.group(1)
        buildingNames.append(building)
    if roomFound:
        room = roomFound.group(1)
        roomNames.append(room)
# add building names
df = df.drop(
    columns=["Schedule", "Loc Cap Calc", "Util Fact", "Opt Fact"]
)  # drop rows with 99 x
df.insert(loc=1, column="Building", value=buildingNames, allow_duplicates=True)
df.insert(
    loc=2, column="Room", value=roomNames, allow_duplicates=True
)  # drop unnecessary columns
dropRowsRegex = re.compile(r"99.*")
filter = df["Listing"].str.contains(dropRowsRegex)
df = df[~filter]

# write dataframe to disk
df.to_pickle("./rooms.pkl")
