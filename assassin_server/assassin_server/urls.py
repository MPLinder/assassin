from django.conf.urls import patterns, include, url
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('assassin_server.views',
    url(r'^assassin/', include('assassin.urls')),
    url(r'^admin/', include(admin.site.urls)),
)
