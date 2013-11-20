from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('assassin.views',
    url(r'^$', 'index', name='index'),
    url(r'^proof_of_concept/$', 'poc', name='poc'),
    url(r'^proof_of_concept/(?P<attempt_id>\d+)/$', 'poc', name='poc_done'),
)