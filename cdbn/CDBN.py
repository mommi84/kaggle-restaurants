#!/usr/bin/env python
# -*- coding: utf-8 -*-

'''
 DBN  w/ continuous-valued inputs (Linear Energy)

 References :
   - Y. Bengio, P. Lamblin, D. Popovici, H. Larochelle: Greedy Layer-Wise
   Training of Deep Networks, Advances in Neural Information Processing
   Systems 19, 2007

'''

import sys
import numpy
import math
import csv
from HiddenLayer import HiddenLayer
from LogisticRegression import LogisticRegression
from RBM import RBM
from CRBM import CRBM
from DBN import DBN
from utils import *
from sklearn.metrics import mean_squared_error as mse

from numpy import genfromtxt
from __builtin__ import xrange
from array import array
from cmath import exp
from scipy.special._ufuncs import exp10

 
class CDBN(DBN):
    def __init__(self, input=None, label=None,\
                 n_ins=2, hidden_layer_sizes=[3, 3], n_outs=2,\
                 numpy_rng=None):
        
        self.x = input
        self.y = label

        self.sigmoid_layers = []
        self.rbm_layers = []
        self.n_layers = len(hidden_layer_sizes)  # = len(self.rbm_layers)

        if numpy_rng is None:
            numpy_rng = numpy.random.RandomState(1234)

        
        assert self.n_layers > 0


        # construct multi-layer
        for i in xrange(self.n_layers):
            # layer_size
            if i == 0:
                input_size = n_ins
            else:
                input_size = hidden_layer_sizes[i - 1]

            # layer_input
            if i == 0:
                layer_input = self.x
            else:
                layer_input = self.sigmoid_layers[-1].sample_h_given_v()
                
            # construct sigmoid_layer
            sigmoid_layer = HiddenLayer(input=layer_input,
                                        n_in=input_size,
                                        n_out=hidden_layer_sizes[i],
                                        numpy_rng=numpy_rng,
                                        activation=sigmoid)
            self.sigmoid_layers.append(sigmoid_layer)

            # construct rbm_layer
            if i == 0:
                rbm_layer = CRBM(input=layer_input,     # continuous-valued inputs
                                 n_visible=input_size,
                                 n_hidden=hidden_layer_sizes[i],
                                 W=sigmoid_layer.W,     # W, b are shared
                                 hbias=sigmoid_layer.b)
            else:
                rbm_layer = RBM(input=layer_input,
                                n_visible=input_size,
                                n_hidden=hidden_layer_sizes[i],
                                W=sigmoid_layer.W,     # W, b are shared
                                hbias=sigmoid_layer.b)
                
            self.rbm_layers.append(rbm_layer)


        # layer for output using Logistic Regression
        self.log_layer = LogisticRegression(input=self.sigmoid_layers[-1].sample_h_given_v(),
                                            label=self.y,
                                            n_in=hidden_layer_sizes[-1],
                                            n_out=n_outs)

        # finetune cost: the negative log likelihood of the logistic regression layer
        self.finetune_cost = self.log_layer.negative_log_likelihood()



def test_cdbn(pretrain_lr=0.01, pretraining_epochs=1000, k=1, \
             finetune_lr=0.01, finetune_epochs=1000):
#     xdata = [[0.4, 0.5, 0.5, 0.,  0.,  0.],
#                      [0.5, 0.3,  0.5, 0.,  0.,  0.],
#                      [0.4, 0.5, 0.5, 0.,  0.,  0.],
#                      [0.,  0.,  0.5, 0.3, 0.5, 0.],
#                      [0.,  0.,  0.5, 0.4, 0.5, 0.],
#                      [0.,  0.,  0.5, 0.5, 0.5, 0.]]
#     print xdata
#     x = numpy.array(xdata)
#     print x
#    
#    y = numpy.array([[1, 0],
#                     [1, 0],
#                     [1, 0],
#                     [0, 1],
#                     [0, 1],
#                     [0, 1]])

#     my_data = numpy.loadtxt('../data/NOT_VALID/train-numericdate-norm-cdbn.csv', delimiter=',')
#     xdata = my_data[:,0:38]

    filez = open('../data/NOT_VALID/train-numericdate-norm-cdbn.csv', 'r')
    data = csv.reader(filez, delimiter=',')
    alldata = [map(float, row) for row in data]
#     print alldata
    
#     xdata = alldata[0][:]
#     print alldata[0:][0:38]
#     print alldata[0:38][0]
#     print xdata
#     ydata = [row for row in data[:,38:48]]
    xydata = numpy.array(alldata)
#     x = numpy.array(xdata)
#     y = numpy.array(ydata)
    x = xydata[:,0:38]
    y = xydata[:,38:48]
#     print [x_ for x_ in x]
#     print [y_ for y_ in y]

    rng = numpy.random.RandomState(123)

    # construct DBN
    dbn = CDBN(input=x, label=y, n_ins=38, hidden_layer_sizes=[30, 30, 30], n_outs=10, numpy_rng=rng)

    # pre-training (TrainUnsupervisedDBN)
    dbn.pretrain(lr=pretrain_lr, k=1, epochs=pretraining_epochs)
    
    # fine-tuning (DBNSupervisedFineTuning)
    dbn.finetune(lr=finetune_lr, epochs=finetune_epochs)


    # test
    #x = numpy.array([[0.5, 0.5, 0., 0., 0., 0.],
    #                 [0., 0., 0., 0.5, 0.5, 0.],
    #                 [0.5, 0.5, 0.5, 0.5, 0.5, 0.]])
    
    # TODO predicting train dataset (for now)
    out = dbn.predict(x)
    print "INPUT"
    for x_ in x:
        print [in_el for in_el in x_]
    print "TARGET"
    for y_ in y:
        print [y_el for y_el in y_]
    print "PREDICTION"
    for o_ in out:
        print [out_el for out_el in o_]
    # normalize outputs
    thr = out.max(axis=0) / 2
    out_norm = numpy.zeros((len(out), len(thr)))
    for i in xrange(0, len(out)):
        for j in xrange(0, len(out[i])):
            out_norm[i][j] = (out[i][j] >= thr[j])
    print out_norm
    # compute revenues
    rev = [math.pow(10, numpy.sum(2**numpy.arange(len(out_))*out_[::-1]) / 100) for out_ in out_norm]
    print rev
    # compute RMSE
    filez2 = open('../data/train-numericdate.csv', 'r')
    data2 = csv.reader(filez2, delimiter=',')
    next(data2, None)
    alldata2 = numpy.array([row for row in data2])
    revenue = map(int, alldata2[:,43])
    print revenue
    print "RMSE = " + str(math.sqrt(mse(rev, revenue)))
    

if __name__ == "__main__":
    test_cdbn()
