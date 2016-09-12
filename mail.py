import smtplib
import string
import argparse

SUBJECT = "PYTHON - Sent using mail.py"
FROM = "mendozagabe.cs441@gmail.com"
#
# Lines 8 - 19 were heavily borrowed from the following link
# http://www.tutorialspoint.com/python/python_command_line_arguments.htm
parser = argparse.ArgumentParser()
parser.add_argument("-s", "--subject", help="Subject for the email to be sent.")
parser.add_argument("-f", "--femail", help="Sender's email address")
args = parser.parse_args()

if args.subject:
   SUBJECT = args.subject

if args.femail:
   FROM = args.femail

print "From: ",FROM
print "To: "
print "Subect: ", SUBJECT

HOST = "smtp.gmail.com"
TO = "mendozagabe.cs441@gmail.com"
PWD = 'b=m6aBJJ3pdnkyUDvFyXq2f;@Q'

text = "Greetings from a python script!"
BODY = string.join((
        "From: %s" % FROM,
        "To: %s" % TO,
        "Subject: %s" % SUBJECT ,
        "",
        text
        ), "\r\n")
server = smtplib.SMTP(HOST, 587)
server.starttls()
server.login(TO, PWD)
server.sendmail(FROM, [TO], BODY)
server.quit()
