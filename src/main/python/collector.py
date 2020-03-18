
"""
Monitor a directory for new files, convert them to text output format, split them to small chunks of bytes, serialize
and publish to Kafka cluster
"""

import json
import logging
import os
import pipelines
import shutil
import sys
import time

from argparse           import ArgumentParser
#
#
#  imports from common


class DistributedCollector:
    """
        Distributed Collector manages the process to collect and publish all the files to the Kafka cluster.
        :param datatype       : Type of data to be collected
        :param topic          : Name of topic where the messages will be published
        :param skip_conversion: If 'True', then no transformation will be applied to the data.
    """

    def __init__(self, datatype, topic, skip_conversion):
        self._logger            = logging.getLogger('SPOT.INGEST.COLLECTOR')
        self._logger.info('Initializing Distributed collector process....')


