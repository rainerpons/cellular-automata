#!/bin/sh

echo "Installing git hooks..."
cp scripts/git-hooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
echo "Done!"
