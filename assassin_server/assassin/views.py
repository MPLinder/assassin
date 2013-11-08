from django.shortcuts import render


def poc(request):
    context = {'hello': 'Hello World'}
    return render(request, 'assassin/poc.html', context)