import constants
import json
import tasks
import urllib
from functools import wraps

import forms
import models
import utils

from allauth.socialaccount import providers
from allauth.socialaccount.models import SocialLogin, SocialToken, SocialApp, SocialAccount
from allauth.socialaccount.providers.facebook.views import fb_complete_login
from allauth.socialaccount.helpers import complete_social_login

from django.views.decorators.csrf import ensure_csrf_cookie
from django.contrib.auth.models import User
from django.core.urlresolvers import reverse
from django.http import HttpResponseRedirect, HttpResponse, HttpResponseNotAllowed
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


def base_context(request):
    context = {'full_name': request.user.get_full_name()}
    return context


def render_response(request, template=None, context=None):
    if request.REQUEST.get('type') == 'json':
        if context is not None and 'form' in context.keys():
            context.pop('form')
        return HttpResponse(json.dumps(context), content_type='application/json')
    else:
        return render(request, template, context)


@get_or_create_fb_user
def index(request):
    if utils.is_trained(request.user):
        # TODO: redirect to wherever when it's ready
        return HttpResponseRedirect(reverse('train'))
    else:
        return HttpResponseRedirect(reverse('train'))


@get_or_create_fb_user
@ensure_csrf_cookie
def train(request):
    context = base_context(request)
    if request.method == 'POST':
        form = forms.TrainingImageForm(request.POST, request.FILES)
        if form.is_valid():
            trainer = form.save(commit=False)
            trainer.user = request.user
            trainer.save()

            context['status'] = 'success'
        else:
            context['status'] = 'error'
            context['errors'] = form.errors

        context['form'] = form
    else:
        form = forms.TrainingImageForm()
        context['form'] = form

    trainers_completed = models.TrainingImage.objects.filter(user=request.user).count()
    context['trainers_completed'] = trainers_completed
    context['trainers_required'] = constants.TRAINING_IMAGES_REQUIRED

    if trainers_completed >= constants.TRAINING_IMAGES_REQUIRED:
        tasks.train.delay(request.user)
        
    return render_response(request, template='assassin/train.html', context=context)


@get_or_create_fb_user
def friends(request):
    fb_friends = utils.get_fb_friends(request.user)

    friends = []
    for friend in fb_friends:
        try:
            social_account = SocialAccount.objects.get(provider='facebook',
                                                       uid=friend['id'],
                                                       user__is_active=True)
        except SocialAccount.DoesNotExist:
            continue

        if utils.is_trained(social_account.user):
            friends.append({
                'name': friend['name'],
                'id': str(social_account.user.id),
                'picture': friend['picture']['data']['url']
            })

    return render_response(request, context={'friends': friends})


@get_or_create_fb_user
def leaderboard(request):
    # TODO: This is horribly inefficient. If I ever want to scale this, fix it.
    fb_friends = utils.get_fb_friends(request.user)

    friends = []
    for friend in fb_friends:
        try:
            social_account = SocialAccount.objects.get(provider='facebook',
                                                       uid=friend['id'],
                                                       user__is_active=True)
        except SocialAccount.DoesNotExist:
            continue

        if utils.is_trained(social_account.user):

            friends.append({
                'name': friend['name'],
                'id': str(social_account.user.id),
                'picture': friend['picture']['data']['url'],
                'points': utils.calculate_points(social_account.user)
            })

    social_account = request.user.socialaccount_set.get(provider='facebook')
    user_info = {
        'name': request.user.get_full_name(),
        'id': str(request.user.id),
        'picture': social_account.get_avatar_url(),
        'points': utils.calculate_points(social_account.user)
    }

    friends.append(user_info)
    friends = sorted(friends, key=lambda x: x['points'], reverse=True)[:10]

    context = {
        'friends': friends,
        'user': user_info,
    }

    return render_response(request, context=context)


@get_or_create_fb_user
@ensure_csrf_cookie
def attempt(request, attempt_id=None):
    context = base_context(request)
    if request.method == 'POST':
        form = forms.AttemptForm(request.POST, request.FILES)
        if form.is_valid():
            attempt = form.save(commit=False)
            attempt.from_user = request.user
            attempt.save()
            context['attempt_id'] = attempt.id
            context['attempt_url'] = attempt.image.url

            confidence, success = attempt.get_confidence()
            context['confidence_level'] = confidence
            context['success'] = success
            context['success_percent'] = constants.SUCCESS_PERCENT
            context['success_points'] = constants.SUCCESS_POINTS
        else:
            context['form'] = form
    else:
        context['form'] = forms.AttemptForm()
        context['success_percent'] = constants.SUCCESS_PERCENT

        if attempt_id:
            attempt = models.Attempt.objects.get(id=int(attempt_id))
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
        form = forms.AttemptForm(request.POST, request.FILES)
        if form.is_valid():
            attempt = form.save(commit=False)
            attempt.from_user = User.objects.get(username='poc_user')
            attempt.save()
            return HttpResponseRedirect(reverse('poc_done',
                                                kwargs={'attempt_id': attempt.id}))
    else:
        mplinder = User.objects.get(username='mplinder')
        imgs = models.TrainingImage.objects.filter(user=mplinder)
        context['img_urls'] = [img.image.url for img in imgs]
        context['form'] = forms.POCForm()
        context['success_percent'] = constants.SUCCESS_PERCENT

        if attempt_id:
            attempt = models.Attempt.objects.get(id=int(attempt_id))
            context['attempt_id'] = attempt_id
            context['attempt_url'] = attempt.image.url

            confidence, success = attempt.get_confidence()
            context['confidence_level'] = confidence
            context['success'] = success

    return render(request, 'assassin/poc.html', context)
