import os

filename = input("Enter the filename: ")
path_input = "org_output/" + filename




input = []
with open(path_input, 'r') as file:
    char = file.read(1)
    if char!=' ' and char!='\n' and char!='\t' and char!='\r':
        input.append(char)
    while char:
        char = file.read(1)
        if char!=' ' and char!='\n' and char!='\t' and char!='\r':
            input.append(char)
# print(input)

# print("\n\n\n\n")

path_output = "output/"+filename
output = []
with open(path_output, 'r') as file:
    char = file.read(1)
    if char!=' ' and char!='\n' and char!='\t' and char!='\r':
        output.append(char)
    while char:
        char = file.read(1)
        if char!=' ' and char!='\n' and char!='\t' and char!='\r':
            output.append(char)
#print(output)

if input == output:
    print("Testcase passed")
else:
    print("Testcase failed")
