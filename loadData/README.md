These Python scripts create the database fed into Firebase. They're supposed to be run every quarter.
Getting your environment up:
```bash
$ sudo apt-get install python3 python3-dev python3-pip
$ pip3 install --user pipenv
```
At this point, you might have to add ~/.local to your PATH. Do so by adding the following line to your ~/.profile
```
export PATH="$PATH:/home/\<username>/.local"
```
Running the scripts
createDF.py generates rooms.pkl, containing the result of web parsing
parseICS.py generates polyrooms.json containing the Firebase data
```bash
pipenv run python createDF.py
pipenv run python parseICS.py
#prettify json for humans
python -m json.tool polyrooms.json  > polyroomspretty.json
```
