import json
import sys
import dateutils
import timeutil


def append_date_to_map(instring):
    "append something to string"
    dat = json.loads(instring)
    dat.setdefault('transforms',[]).append(timeutil.getStr())
    dat['hello'] = 'hi'
    return json.dumps(dat)