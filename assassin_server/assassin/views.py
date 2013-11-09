from forms import AttemptForm, POCForm
from models import Attempt, TrainingImage

from django.contrib.auth.models import User
from django.core.urlresolvers import reverse
from django.http import HttpResponseRedirect
from django.shortcuts import render


def poc(request, attempt_id=None):
    context = {}
    if request.method == 'POST':
        request.POST['from_user'] = str(User.objects.get(username='poc_user').id)
        request.POST['to_user'] = str(User.objects.get(username='mplinder').id)
        form = AttemptForm(request.POST, request.FILES)
        if form.is_valid():
            attempt = form.save()
            return HttpResponseRedirect(reverse('poc_done',
                                                kwargs={'attempt_id': attempt.id}))
    else:
        mplinder = User.objects.get(username='mplinder')
        imgs = TrainingImage.objects.filter(user=mplinder)
        context['img_urls'] = [img.image.url for img in imgs]
        context['form'] = POCForm()

        if attempt_id:
            attempt = Attempt.objects.get(id=int(attempt_id))
            context['attempt_id'] = attempt_id
            context['attempt_url'] = attempt.image.url

            confidence, success = attempt.get_confidence()
            context['confidence_level'] = confidence
            context['success'] = success

    return render(request, 'assassin/poc.html', context)
