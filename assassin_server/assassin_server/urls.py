from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('assassin_server.views',
    # Examples:
    # url(r'^$', 'assassin_server.views.home', name='home'),
    url(r'^proof_of_concept/', 'poc', name='poc'),

    url(r'^admin/', include(admin.site.urls)),
)
