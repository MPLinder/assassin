from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('assassin.views',
    url(r'^proof_of_concept/', 'poc', name='poc'),
)