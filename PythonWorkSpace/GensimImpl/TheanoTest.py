import theano, numpy
from theano import tensor as T

# nv :: size of our vocabulary
# de :: dimension of the embedding space
# cs :: context window size
nv, de, cs = 1000, 50, 5

embeddings = theano.shared(0.2 * numpy.random.uniform(-1.0, 1.0, \
    (nv+1, de)).astype(theano.config.floatX)) # add one for PADDING at the end

idxs = T.imatrix() # as many columns as words in the context window and as many lines as words in the sentence
x    = self.emb[idxs].reshape((idxs.shape[0], de*cs))

