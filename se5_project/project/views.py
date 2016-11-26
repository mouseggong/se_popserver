from django.shortcuts import render
from .models import * 

# Create your views here.

def index(request):
	common_data = CommonData("Main Page")
	return render(request,'index.html',{'common':common_data})

def second(request,bubbleword):
	common_data = CommonData("Second Page")
	return render(request,'second.html',{'common':common_data,'bubbleword':bubbleword})

def third(request,keyword):
	common_data = CommonData("Third Page")
	return render(request,'third.html',{'common':common_data,'keyword':keyword})