# Contributing Guidelines

This documentation contains set of guidelines to help you during the contribution process.

# Submitting Contributions👨🏻‍💻
Below you will find the process and workflow used to review and merge your changes.
## 🌟 : Choose an issue/ Create an issue

- Look for the existing issue or create your own issue.
- Comment on the respective issue you would like to work before creating a Pull Request.
- Wait for the issue to be assigned to you after which you can start working on it.

## 🌟 : Fork the repository

- Fork this repository "ImageToolbox" by clicking on the "Fork" button. This will create a local copy of this respository on your GitHub profile.

## 🌟 : Clone the forked repository

- Once the repository is forked you need to clone it to your local machine.
- Click on the "Code" button in the repository page and copy the link provided in the dropdown menu.


```bash
git clone https://github.com/<your-username>/<ImageToolbox>  
```

- Keep a reference to the original project in `upstream` remote.

```bash  
cd <repo-name>  
git remote add upstream https://github.com/<upstream-owner>/<ImageToolbox>
git remote -v # To the check the remotes for this repository 
```  

- If the project is forked already, update the copy before working.

```bash
git remote update
git checkout <branch-name>
git rebase upstream/<branch-name>
``` 

## 🌟 : Create a new branch

- Always create a new branch and name it accordingly so as to identify the issue you are addressing.

```bash
# It will create a new branch with name branch_name and switch to that branch 
git checkout -b branch_name
```
## 🌟 : Work on the issue assigned

- Work on the issue(s) assigned to you, make the necessary changes in the files/folders needed.
- After making the changes add them to the branch you've created.

```bash  
# To add all new files to branch Branch_Name  
git add .  

# To add only a few files to Branch_Name
git add <file name>
```
## 🌟 : Commit the changes

- Add your commits.
- Along with the commit give a descriptive message that reflects your changes.

```bash
git commit -m "message"  
```
- Note : A Pull Request should always have only one commit. 

## 🌟 : Push the changes

- Push the committed changes in your branch to your remote repository.

```bash  
git push origin branch_name
```
## 🌟 : Create a Pull Request

- Go to your repository in the browser and click on compare and pull request.
- Add a title and description to your pull request that best describes your contribution.
- After which the pull request will be reviewed and the maintainer will provide the reviews required for the changes.

If no changes are needed, this means that your Pull Request has been reviewed and will be merged to the original code base by the maintainer.

Happy Hacking!