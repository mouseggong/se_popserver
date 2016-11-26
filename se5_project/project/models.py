from __future__ import unicode_literals

from django.db import models

# Create your models here.

class CommonData:
	host = "http://localhost:8000"
	title = ""

	def __init__(self,title):
		self.title = title