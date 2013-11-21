import utils

from celery import task

@task
def train(user):
    utils.train_user_images(user)

@task
def test():
	return True
