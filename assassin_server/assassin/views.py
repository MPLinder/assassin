import constants
import json
import tasks
from forms import AttemptForm, POCForm
from functools import wraps
from models import Attempt, TrainingImage

from allauth.socialaccount import providers
from allauth.socialaccount.models import SocialLogin, SocialToken, SocialApp
from allauth.socialaccount.providers.facebook.views import fb_complete_login
from allauth.socialaccount.helpers import complete_social_login

from django.views.decorators.csrf import ensure_csrf_cookie
from django.contrib.auth.models import User
from django.core.urlresolvers import reverse
from django.http import HttpResponseRedirect, HttpResponse
from django.shortcuts import render


def get_or_create_fb_user(view_func):
    @wraps(view_func)
    def _wrapped_view(request, *args, **kwargs):
        if request.user.is_authenticated():
            return view_func(request, *args, **kwargs)

        access_token = request.REQUEST.get('access_token', '')
        try:
            app = SocialApp.objects.get(provider='facebook')
            token = SocialToken(app=app, token=access_token)
            login = fb_complete_login(request, app, token)
            login.token = token
            login.state = SocialLogin.state_from_request(request)
            complete_social_login(request, login)
            return view_func(request, *args, **kwargs)
        # TODO: what to except here?
        except:
            context = {'error': 'Unable to get or create facebook user.'}
            return render_response(request, 'assassin/login.html', context)

    return _wrapped_view


def render_response(request, template=None, context=None):
    if request.REQUEST.get('type') == 'json':
        if context is not None and 'form' in context.keys():
            context.pop('form')
        return HttpResponse(json.dumps(context), content_type='application/json')
    else:
        return render(request, template, context)


@get_or_create_fb_user
def index(request):
    if request.user.is_authenticated():
        context = {'full_name': request.user.get_full_name()}
        return render_response(request, 'assassin/index.html', context)
    else:
        return render_response(request, 'assassin/login.html')


@get_or_create_fb_user
@ensure_csrf_cookie
def attempt(request, attempt_id=None):
    context = {}
    if request.method == 'POST':
        form = AttemptForm(request.POST, request.FILES)
        if form.is_valid():
            attempt = form.save(commit=False)
            attempt.from_user = request.user
            attempt.save()
            return HttpResponseRedirect(reverse('attempt_done',
                                                kwargs={'attempt_id': attempt.id}))
    else:
        context['form'] = AttemptForm()
        context['success_percent'] = constants.SUCCESS_PERCENT

        if attempt_id:
            attempt = Attempt.objects.get(id=int(attempt_id))
            context['attempt_id'] = attempt_id
            context['attempt_url'] = attempt.image.url

            confidence, success = attempt.get_confidence()
            context['confidence_level'] = confidence
            context['success'] = success

    return render_response(request, 'assassin/attempt.html', context)


def poc(request, attempt_id=None):
    context = {}
    if request.method == 'POST':
        request.POST['to_user'] = str(User.objects.get(username='mplinder').id)
        form = AttemptForm(request.POST, request.FILES)
        if form.is_valid():
            attempt = form.save(commit=False)
            attempt.from_user = User.objects.get(username='poc_user')
            attempt.save()
            return HttpResponseRedirect(reverse('poc_done',
                                                kwargs={'attempt_id': attempt.id}))
    else:
        mplinder = User.objects.get(username='mplinder')
        imgs = TrainingImage.objects.filter(user=mplinder)
        context['img_urls'] = [img.image.url for img in imgs]
        context['form'] = POCForm()
        context['success_percent'] = constants.SUCCESS_PERCENT

        if attempt_id:
            attempt = Attempt.objects.get(id=int(attempt_id))
            context['attempt_id'] = attempt_id
            context['attempt_url'] = attempt.image.url

            confidence, success = attempt.get_confidence()
            context['confidence_level'] = confidence
            context['success'] = success

    return render(request, 'assassin/poc.html', context)
