words = ['cat', 'mouse', 'monkey']

for w in words:
    print(w, str(len(w)))

for w in words[:]:
    if len(w) > 5:
        words.insert(0, w)


print(words)

for w in words:
    print('post', w, str(len(w)))

for i in range(4):
    print(i)


for i in range(len(words)):
    print(i, words[i])

# x = input("Enter integer:")
# print(x)


print(range(10))

print(list(range(5)))

global a, b
a, b = 0, 1

print(a, b)


def fib(n):
    a, b = 0, 1
    while a < n:
        print(a, end=' ')
        a, b = b, a+b
    print()

print(fib(2000))


def fib2(n):
    result = []
    a, b = 0, 1
    while a < n:
        result.append(a)
        # result = result + [a]
        a, b = b, a+b
    return result

f100 = fib2(100)
print(f100)


def f(a, L=[]):
    L.append(a)
    return L

print(f(1))
print(f(2))
print(f(3))

squares = [1, 4, 9, 16, 25]

print(squares)

print(squares[1])
print(squares[-2])
print(squares[-3:])

print("""\
Usage: thingy [OPTIONS]
     -h                        Display this usage message
     -H hostname               Hostname to connect to
""")

from collections import deque
d = deque(["task1", "task2", "task3"])
d.append("task4")
print("Handling", d.popleft())
print(d)

print("Handling", d.pop())
print(d)

from heapq import heapify, heappop, heappush
data = [1, 3, 5, 7, 9, 2, 4, 6, 8, 0]
heapify(data)
print(data)
heappush(data, -5)
print(data)
[heappop(data) for i in range(3)]
print(data)


import logging
logging.debug('Debugging information')
logging.info('Informational message')
logging.warning('Warning test')
logging.error('Error test')
logging.critical('Critical test')

import csv
with open('resources/out.csv') as f:
    reader = csv.reader(f)
    for row in reader:
        print(row)


import csv
with open('resources/some.csv', 'w', newline='') as fw:
    writer = csv.writer(fw)
    writer.writerows(row)

import os
print(os.getcwd())

import shutil
shutil.copyfile('resources/some.csv', 'resources/some2.csv')

import numpy as np
A = np.mat('[1 2;3 4]')
print(A)

# from scipy import linalg
# print(linalg.inv(A))

# import sympy as sy
# print(sy.sqrt(8))

# import matplotlib.pyplot as plt
# plt.plot([1,2,3,4], [1,4,9,16], 'ro')
# plt.axis([0, 6, 0, 20])
# plt.show()


def average(values):
    """Computes the arithmetic mean of a list of numbers.

    >>> print(average([20, 30, 70]))
    40.0
    """
    return sum(values) / len(values)

import doctest
print(doctest.testmod())
print(average([20,30,40]))

a = [66.25, 333, 333, 1, 1234.5]
a.remove(333)
print(a)

a.sort()
print(a)

squares = [x**2 for x in range(10)]
print(squares)

matrix = [
     [1, 2, 3, 4],
     [5, 6, 7, 8],
     [9, 10, 11, 12],
]

print(matrix)

a = [-1, 1, 66.25, 333, 333, 1234.5]
del a[0]
print(a)
del a[:]
print(a)
del a


tuple = 12345, 54321, 'hello'
print(tuple)

newset = {'apple', 'orange', 'apple', 'pear', 'orange', 'banana'}
print(newset)
print('orange' in newset)

seta = set('abracadabra')
print(seta)

ba = {x for x in 'abracadabra' if x not in 'abc'}
print(ba)

newdict = {'jack': 4098, 'sage': 4139}
print(newdict)
newdict['vaibhav'] = 5525
print(newdict)
print(newdict['sage'])
print(list(newdict.keys()))
print(sorted(newdict.keys()))

newdict2 = dict(sape=4139, guido=4127, jack=4098)
print(newdict2)
for k, v in newdict2.items():
    print(k, v)

for i, v in enumerate(newdict2):
    print(i, v)

import fibonacci as f11
f11.fibon(1000)
print(f11.__name__)

print('{0} and {1}'.format('spam', 'eggs'))
contents = 'eels'
print('My hovercraft is full of {!r}.'.format(contents))

print('start file')
f = open('resources/out.csv')
for line in f:
    print(line)
print('end file')

f = open('resources/workfile', 'rb+')
print(f.write(b'abcdefghijklmnop'))
print(f.seek(5))
print(f.read(1))
print(f.seek(-3, 2))
print(f.read(1))
f.close()

with open('resources/out.csv') as f:
    print(f.read())
print('File auto closed :', f.closed)

import json
x = [1, 'simple', 'list']
print(json.dumps(x))

f = open('resources/workfile', 'r+')
print(json.dump(x, f))


try:
    x = int('a')
except ValueError as err:
    print("Oops Invalid number. Try again... :", err)
else:
    print("No error")

try:
    raise Exception('spam', 'eggs')
except Exception as inst:
    print(type(inst))
    print(inst.args)
    print(inst)
finally:
    print('inside finally')

# raise NameError("Hi there")


class MyClass:
    """A simple example class"""
    i = 12345

    def __init__(self, name="no name"):
        print('init called')
        self.name = name

    def f(self):
        return 'Hello World'

print(MyClass.i)
print(MyClass.f(MyClass))

objA = MyClass()
print(objA.__doc__)

objB = MyClass('VK')
print(objB.name)
print(objB.__class__)


print(issubclass(bool, int))
print(issubclass(float, int))

print(isinstance(objA, MyClass))


class MyClassChild(MyClass):
    def __init__(self, name="no name"):
        print('init child called')
        self.name = name

objChild = MyClassChild()













