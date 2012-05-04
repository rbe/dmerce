#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.3 $'
#
# Revision 1.1  2000-11-22 14:35:55+01  rb
# Initial revision
#
##

import sys
import string
import types

class Path:

    """ paths """

    def __init__(self, list):
        self.__List = list

        def __getitem__(self, path):
            """ get a list item depending on a path """
            object = self.__List
            try:
                for item in path:
                    object = object[item]
                return object
            except:
                return None

    def __setitem__(self, path, value):
        """ set a list item depending on a path """
        object = self.__List
        separate = []
        for item in path:
            object = object[item]
            separate.append(object)
        print 'separate-list:', separate
        # Change value of item located at path
        separate[len(separate) - 1] = value
        # Glue separate items of list together to restore list with modified value
        newlist = []
        for item in separate:
            newlist.append(item)
        self.__List = newlist

class Tree:

    """ handle trees """

    def __init__(self, title = '', node = []):
        self.__Title = title
        self.__Nodes = node
        self.__Index = len(node)

    def __delitem__(self, index):
        """ delete a node from position 'index' """
        del self.__Nodes[index]
        self.__Index = self.__Index - 1

    def __getitem__(self, index):
        """ return a node from position 'index' """
        return self.__Nodes[index]

    def __setitem__(self, index, value):
        """ change a node """
        self.__Nodes[index] = value

    def __len__(self):
        """ length: count of nodes """
        return len(self.__Nodes)

    def __repr__(self):
        """ me, myself and I """
        return 'Tree(\'%s\', %s)' % (self.__Title, self.__Nodes)

    def Add(self, index, node):
        """
        Appends or inserts a node
        - Appends, when index is None
        - Inserts, when index is a integer value
        """
        if index is None:
            self.__Nodes.append(node)
        elif type(index) is types.IntType:
            self.__Nodes.insert(index, node)
        self.__Index = self.__Index + 1

    def GetPathOf(self, thing):
        """ return path of an object in the whole tree """
        return

    def Move(self, src, dst):
        """
        Move an item from source to destination
        src and dst are valid paths (see class Path)
        """
        # Create a path object with our nodes
        P = Path(self.__Nodes)
        # Get source object
        s = P[src]
        # Insert src at dst
        # self.Add(dst, s)
        # Delete dst: now the positions have changed by 1
        # del self.__Nodes[src + 1]

    def NumElements(self):
        """ how much elements do I have """
        return

    def NumSubtrees(self):
        """ how much nodes do I have """
        return

    def PrintTree(self, nodes = 0, prefix = ''):
        """ print a tree of myself """
        if not prefix:
            prefix = '+'
        if not nodes:
            nodes = self.__Nodes
        # Go through all elements of the tree
        for element in range(len(nodes)):
            # List element IS a tree
            if isinstance(nodes[element], Tree):
                # Recursion
                if len(nodes[element]):
                    self.PrintTree(nodes[element], '%s+' % prefix)
            else:
                # List element IS NOT a tree
                print '%s%s' % (prefix, nodes[element])
