# this python code writes a dataframe with the room information to current directory
from bs4 import BeautifulSoup
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
    if(re.match(".*-.*",row['Listing']) is None):
        rowsSkip.append(index+2)
pandasTable = pd.read_html(page.content, header = 0, skiprows = rowsSkip, keep_default_na = False)[0]

# assign ICS href to ICS columns
icsHref = lhTable.xpath('//tr/td//a/@href')
icsRegex = re.compile(r'\/ics\/location.*')
icsHref = list(filter(icsRegex.match, icsHref))
pandasTable["ICS"] = icsHref
# drop unnecessary columns
pandasTable = pandasTable.drop(columns = ['Schedule', 'Loc Cap Calc', 'Util Fact', 'Opt Fact'])

# write dataframe to disk
pandasTable.to_pickle('./rooms.pkl')
pandasTable.to_html('./rooms.html')
