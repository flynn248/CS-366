from itertools import chain, combinations, combinations_with_replacement

def get_attributes():
    # number of elements
    n = int(input("Enter number of elements: "))
    
    # Below line read inputs from user using map() function
    r = list(map(str,input("\nEnter the letters: ").strip().split()))[:n]

    print("\nList is - ", r)
    return r, n

def get_functional_dependencies():
    # number of fds
    n = int(input("Enter number of functional dependencies: "))

    fds_l = list(map(str, input("\nEnter the funcitonal dependencies with a comma in between dependencies.\
                                \n\tEx: AB C,D F,E HI\
                                \nInput: ").strip().split(sep=',')))

    # Separate a functional dependency into a tuple and insert into a list
    fds = []
    for fd in fds_l:
        fds.append(tuple(fd.split()[:]))

    return fds, n

def powerset(iterable):
    r_plus = []
    attr = list(chain.from_iterable(combinations(iterable, r) for r in range(1, len(iterable)+1)))

    for fd in attr:
        r_plus.append(str(fd).replace(',', '').replace('(', '').replace(')', '').replace('\'', '').replace(' ', ''))
    
    return r_plus

def find_closure(func_dependencies, result):
    print("\nResult before loop: %s" % result)
    print("Functional Dependencies: %s" % func_dependencies)

    depencency_exists = False
    result_changed = False

    while True:
        for fd in func_dependencies:
            print("Testing functional dependency: %s" % str(fd))
            num_hits = 0
            result_changed = False
            depencency_exists = False

            for element in fd[0]: # check if attributes makes up a dependency
                if result.count(element) > 0:
                    print("\tGot a HIT!")
                    num_hits += 1
                if num_hits == len(fd[0]):
                    print("\tDependency Exists!")
                    depencency_exists = True

            if depencency_exists: # add attribute to result
                for element in fd[1]:
                    if result.count(element) == 0:
                        print("\tResult Changed!")
                        result.append(element)
                        result_changed = True
        
        if not result_changed:
            break

    print("Result after loop %s" % result)


    return result


def attribute_closure():
    attributes, num_attributes = get_attributes()
    attribute_plus = powerset(attributes)
    func_dependencies, num_func_dependencies = get_functional_dependencies()

    attr_closure = []
    
    for i in range(0, (pow(2,num_attributes) - 1)):
        result = []
        for element in attribute_plus[i]:  # Add attributes of an element from powerset of attributes to result
            for attr in element:
                result.append(attr)
        
        attr_closure.append((attribute_plus[i], find_closure(func_dependencies, result)))

    print(attr_closure)




if __name__ == '__main__':
    #r, n_r = get_attributes()
    #r_plus = powerset(r)
    #print(r_plus)
    #fds, n_fd = get_functional_dependencies()
    #attribute_closure()
#   r = ['A', 'B', 'C', 'D', 'E', 'F']
#    test = ("AB", "C")
#    print(type(test))
#    print(len(test[0]), len(test[1]))
#    for fd in fds:
#        for f in fd[0]:
#            for attr in r:
#                print(f == attr)
    attribute_closure()