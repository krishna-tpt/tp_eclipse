#!/bin/bash

# ================================================
# Git Sync Script - Pull then Push
# Repo  : https://github.com/krishna-tpt/tp_eclipse.git
# Path  : /home/dev021/eclipse
# ================================================

REPO_PATH="/home/dev021/eclipse"
REPO_URL="https://github.com/krishna-tpt/tp_eclipse.git"
BRANCH="main"

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}  Git Sync Started - $(date '+%Y-%m-%d %H:%M:%S')${NC}"
echo -e "${YELLOW}========================================${NC}"

# --- Step 1: Folder check ---
if [ ! -d "$REPO_PATH" ]; then
    echo -e "${RED}ERROR: Folder not found: $REPO_PATH${NC}"
    exit 1
fi

cd "$REPO_PATH" || exit 1

# --- Step 2: Git repo check ---
if [ ! -d ".git" ]; then
    echo -e "${YELLOW}Git repo not found. Start Initializing ...${NC}"
    git init
    git remote add origin "$REPO_URL"
fi

# --- Step 3: Remote URL check ---
CURRENT_REMOTE=$(git remote get-url origin 2>/dev/null)
if [ "$CURRENT_REMOTE" != "$REPO_URL" ]; then
    echo -e "${YELLOW}Remote URL update ...${NC}"
    git remote set-url origin "$REPO_URL"
fi

# --- Step 4: PULL (if remote changes) ---
echo ""
echo -e "${GREEN}[PULL] Remote changes checking...${NC}"

git fetch origin "$BRANCH" 2>/dev/null

LOCAL=$(git rev-parse HEAD 2>/dev/null)
REMOTE=$(git rev-parse origin/$BRANCH 2>/dev/null)

if [ "$LOCAL" = "$REMOTE" ]; then
    echo -e "${GREEN}✓ Already up to date. No need Pull.${NC}"
elif [ -z "$LOCAL" ]; then
    echo -e "${YELLOW}First time pull ...${NC}"
    git pull origin "$BRANCH" --allow-unrelated-histories
else
    echo -e "${YELLOW}Remote changes is there, Pull the changes...${NC}"
    git pull origin "$BRANCH" --allow-unrelated-histories
    if [ $? -ne 0 ]; then
        echo -e "${RED}ERROR: Pull failed, Check if any Conflict is there.${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Pull success.${NC}"
fi

# --- Step 5: Local changes check ---
echo ""
echo -e "${GREEN}[PUSH] Checking Local changes...${NC}"

git add .

# Changes?
if git diff --cached --quiet; then
    echo -e "${GREEN}✓ No local changes. No Push.${NC}"
else
    echo -e "${YELLOW}Changes available. Will Commit & Push...${NC}"

    COMMIT_MSG="Auto sync - $(date '+%Y-%m-%d %H:%M:%S')"
    git commit -m "$COMMIT_MSG"

    git push -u origin "$BRANCH"

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Push success!${NC}"
    else
        echo -e "${RED}ERROR: Push failed.${NC}"
        exit 1
    fi
fi

echo ""
echo -e "${YELLOW}========================================${NC}"
echo -e "${GREEN}  Sync Complete! - $(date '+%Y-%m-%d %H:%M:%S')${NC}"
echo -e "${YELLOW}========================================${NC}"
