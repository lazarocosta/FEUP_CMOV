#!/usr/bin/python3
# coding=utf-8

import os
import subprocess
import sys
import time
import threading

def my_call(args_array):
    if subprocess.call(args_array) != 0:
        print("An error occurred.")
        sys.exit()

if __name__ == "__main__":
    if os.geteuid() != 0:
        my_file_path = os.path.abspath(sys.argv[0])
        print("Running this script as root.")
        my_call(["sudo", "python3", my_file_path])
        quit()
    else:
        my_call(["apt-get", "update"])
        my_call(["apt-get", "install", "-y", "curl"])
        my_call(["curl", "-sL", "https://deb.nodesource.com/setup_6.x", "-o", "nodesource_setup.sh"])
        my_call(["sh", "nodesource_setup.sh"])
        my_call(["apt-get", "install", "-y", "nodejs"])
        os.chdir("functions")
        my_call(["npm", "install", "--save", "firebase-functions@latest"])
        my_call(["npm", "install", "-g", "firebase-tools@latest"])
        my_call(["npm", "install"])
        my_call(["firebase", "login", "--no-localhost"])
        hostname = input("Please, indicate the server hostname/IP: ")
        threading.Thread(target=my_call, args=([["firebase", "serve", "--only", "functions", "-o", hostname, "-p", "5000"]])).start()
        time.sleep(20)
        os.chdir("..")
        my_call(["curl", "-X", "POST", "http://localhost:5000/cmov-d52d6/us-central1/addPerformances", "--data", "'{}'", "-g", "-H", "\"Content-Type: application/json\""])
        my_call(["curl", "-X", "POST", "http://localhost:5000/cmov-d52d6/us-central1/addProducts", "--data", "'{}'", "-g", "-H", "\"Content-Type: application/json\""])
