import requests

obj = requests.get("http://api.conceptnet.io/c/en/car").json()
print(obj.keys())