import lxml.html as LH
import pandas as pd
import requests
import re

url = "http://schedules.calpoly.edu/all_location_curr.htm"
page = requests.get(url)
content = page.content

# remove rows with nonsense data
rowsSkip = [0]
pandasTable = pd.read_html(content, header = 0, skiprows = rowsSkip, keep_default_na = False)[0]
lhTable = LH.fromstring(content)

for index, row in pandasTable.iterrows():
  if (re.match(".*-.*", row['Listing']) is None):
    rowsSkip.append(index + 2)

pandasTable = pd.read_html(page.content, header = 0, skiprows = rowsSkip, keep_default_na = False)[0]# assign ICS href to ICS columns
icsHref = lhTable.xpath('//tr/td//a/@href')
icsRegex = re.compile(r'\/ics\/location.*')
icsHref = list(filter(icsRegex.match, icsHref))
pandasTable["ICS"] = icsHref# get building names
buildingNames = []
roomNames = []
buildingRegex = re.compile(r'(.+?)[-\s].*')
roomRegex = re.compile(r'.*[-](\w+)(.*)')
for index, row in pandasTable.iterrows():
  text = row.Listing
  buildingFound = buildingRegex.search(text)
  roomFound = roomRegex.search(text)
  if buildingFound:
    building = buildingFound.group(1)
    buildingNames.append(building)
  if roomFound:
    room = roomFound.group(1)
    roomNames.append(room)
#print(roomNames)
#print(buildingNames)
# add building names
pandasTable = pandasTable.drop(columns = ['Schedule', 'Loc Cap Calc', 'Util Fact', 'Opt Fact'])# drop rows with 99 x
pandasTable.insert(loc = 1, column = 'Building', value = buildingNames, allow_duplicates = True)
pandasTable.insert(loc = 2, column = 'Room', value = roomNames, allow_duplicates = True)# drop unnecessary columns
dropRowsRegex = re.compile(r'99.*')
filter = pandasTable['Listing'].str.contains(dropRowsRegex)
pandasTable = pandasTable[~filter]

# write dataframe to disk
pandasTable.to_pickle('./rooms.pkl')
pandasTable.to_html('./rooms.html')
