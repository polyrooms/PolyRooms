from bs4 import BeautifulSoup
import requests
url = "http://schedules.calpoly.edu/all_location_curr.htm"
r=requests.get(url)
soup=BeautifulSoup(r.content,'html.parser')
divisions = soup.find_all("div")
for count,division in enumerate(divisions):
    print("division number {} is {}".format(count, division))
