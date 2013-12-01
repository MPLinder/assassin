import cv2
import numpy
import requests
import sys

import constants
import models

from django.conf import settings


# cv2.imread(img.image.file.name)
def read_image(image_file):
    """
    Reads and resizes the given image

    Args:
        image: A file object.

    Returns:
        The image, which is a numpy array.
    """
    try:
        img = cv2.imread(image_file.name, cv2.IMREAD_GRAYSCALE)
        img = cv2.resize(img, (1000, 1000))
        result = numpy.asarray(img, dtype=numpy.uint8)
    except IOError, (errno, strerror):
        print "I/O error({0}): {1}".format(errno, strerror)
    except:
        print "Unexpected error:", sys.exc_info()[0]
        raise

    return result


def get_confidence_level(to_user, image):
    attempt = read_image(image.file)

    target = []
    labels = []
    for training_image in to_user.trainingimage_set.all():
        target.append(read_image(training_image.image.file))
        labels.append(0)

    model = cv2.createLBPHFaceRecognizer()
    model.load(settings.MODEL_IMAGES_ROOT + to_user.username + '.yml')

    [label, confidence] = model.predict(numpy.asarray(attempt))

    return confidence


def train_user_images(user):
    target = []
    labels = []
    for training_image in user.trainingimage_set.all():
        target.append(read_image(training_image.image.file))
        labels.append(0)

    model = cv2.createLBPHFaceRecognizer()
    model.train(numpy.asarray(target), numpy.asarray(labels))
    model.save(settings.MODEL_IMAGES_ROOT + user.username + '.yml')


def scale(val, src, dst):
    """
    Scale the given value from the scale of src to the scale of dst.
    """
    return ((val - src[0]) / (src[1]-src[0])) * (dst[1]-dst[0]) + dst[0]


def is_trained(user):
    return models.TrainingImage.objects.filter(user=user).count() >= \
           constants.TRAINING_IMAGES_REQUIRED


def get_fb_friends(user):
    social_account = user.socialaccount_set.get(provider='facebook')
    token = social_account.socialtoken_set.all()[0]

    url = settings.FACEBOOK_FRIENDS_URL.format(social_account.uid)
    params = {
        'access_token': token.token,
        'fields': 'name,id,picture'
    }

    friends = []
    while url:
        resp = requests.get(url=url, params=params).json()
        friends.extend(resp.get('data', []))
        url = resp['paging'].get('next')

    return friends


def calculate_points(user):
    points = 0
    attempts = models.Attempt.objects.filter(from_user=user)

    for attempt in attempts:
        if attempt.get_confidence() >= constants.SUCCESS_PERCENT:
            points += constants.SUCCESS_POINTS

    return points