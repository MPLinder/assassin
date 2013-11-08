import cv2
import numpy
import sys


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
    model.train(numpy.asarray(target), numpy.asarray(labels))

    [label, confidence] = model.predict(numpy.asarray(attempt))

    return confidence