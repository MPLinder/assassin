"""
Django settings for assassin_server project.

For more information on this file, see
https://docs.djangoproject.com/en/1.6/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/1.6/ref/settings/
"""
import logging
# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os
BASE_DIR = os.path.dirname(os.path.dirname(__file__))

MEDIA_ROOT = os.path.normpath(os.path.join(os.path.dirname(__file__), '../../../media/').replace('\\','/'))
STATIC_ROOT = os.path.normpath(os.path.join(os.path.dirname(__file__), '../../../static/').replace('\\','/'))
MODEL_IMAGES_ROOT = MEDIA_ROOT + '/models/'

logging.basicConfig(level=logging.DEBUG,
    format='%(asctime)s %(levelname)s %(message)s',
    filename=os.path.join(BASE_DIR, 'django.log'),
    filemode='a+')

# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/1.6/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = 'oro7^&d(5_f4n#0%-_t+=5klt5cbzzlt)qf^lg-2w6#-@v(2ii'

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

TEMPLATE_DEBUG = True

ALLOWED_HOSTS = ['wonderflonium.net', 'www.wonderflonium.net']

ADMINS = ('Michael Linder', 'mplinder@utexas.edu')

LOGIN_REDIRECT_URL = '/'

# Application definition

INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'django.contrib.sites',
    'assassin',
    'south',
    'allauth',
    'allauth.account',
    'allauth.socialaccount',
    'allauth.socialaccount.providers.facebook',
    'djsupervisor',
    'djcelery',
)

MIDDLEWARE_CLASSES = (
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
)

TEMPLATE_CONTEXT_PROCESSORS = (
    'django.contrib.auth.context_processors.auth',
    "django.core.context_processors.request",
    "allauth.account.context_processors.account",
    "allauth.socialaccount.context_processors.socialaccount",
)

AUTHENTICATION_BACKENDS = (
    # Needed to login by username in Django admin, regardless of `allauth`
    "django.contrib.auth.backends.ModelBackend",
    # `allauth` specific authentication methods, such as login by e-mail
    "allauth.account.auth_backends.AuthenticationBackend",
)

SOCIALACCOUNT_PROVIDERS = {
    'facebook':
        {'SCOPE': ['email', 'publish_stream'],
         'AUTH_PARAMS': {'auth_type': 'reauthenticate'},
         'METHOD': 'oauth2',
         'LOCALE_FUNC': 'path.to.callable'}
}

ROOT_URLCONF = 'assassin_server.urls'

WSGI_APPLICATION = 'assassin_server.wsgi.application'


# Internationalization
# https://docs.djangoproject.com/en/1.6/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'UTC'

USE_I18N = True

USE_L10N = True

USE_TZ = True


# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.6/howto/static-files/

STATIC_URL = '/static/'
MEDIA_URL = '/media/'

EMAIL_HOST = 'smtp.webfaction.com'
EMAIL_HOST_USER = 'mailbox_username'
EMAIL_HOST_PASSWORD = 'mailbox_password'
DEFAULT_FROM_EMAIL = 'valid_email_address'
SERVER_EMAIL = 'valid_email_address'

FACEBOOK_GRAPH_URL = 'https://graph.facebook.com/'
FACEBOOK_FRIENDS_URL = FACEBOOK_GRAPH_URL + '{0}/friends/'

try:
    from settings_local import *
except ImportError:
    pass
