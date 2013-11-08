from forms import AttemptForm, POCForm
from models import Attempt

from django.contrib.auth.models import User
from django.core.urlresolvers import reverse
from django.http import HttpResponseRedirect
from django.shortcuts import render


def poc(request, attempt_id=None):
    context = {}
    # cv2.imread(img.image.file.name)
    if request.method == 'POST':
        request.POST['from_user'] = str(User.objects.get(username='poc_user').id)
        request.POST['to_user'] = str(User.objects.get(username='mplinder').id)
        form = AttemptForm(request.POST, request.FILES)
        if form.is_valid():
            attempt = form.save()
            return HttpResponseRedirect(reverse('poc_done',
                                                kwargs={'attempt_id': attempt.id}))
    else:
        if attempt_id:
            attempt = Attempt.objects.get(id=int(attempt_id))
            context['attempt_id'] = attempt_id
            context['confidence_level'] = attempt.confidence_level
        else:
            form = POCForm()
            context['form'] = form

    return render(request, 'assassin/poc.html', context)
