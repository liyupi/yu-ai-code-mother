#!/usr/bin/env python3
"""
GitHub Organization Repository Access Audit Tool
=================================================
This script enumerates all access pathways for every repository in a GitHub
organization and generates a report that makes it easy to verify whether
specific users (e.g. departed employees) can still reach any repository.

Access pathways checked per repository:
  1. Direct outside-collaborators (invited individually at the repo level)
  2. Team memberships   – every team that has access to the repo, and every
                          member of those teams
  3. Organization members – role (owner / member) and their resulting access

Usage
-----
  export GITHUB_TOKEN="ghp_xxxxxxxxxxxxxxxxxxxx"   # PAT with read:org + repo scope
  python3 github_org_access_audit.py \\
      --org yuyuanweb \\
      --check-users "alice,bob,charlie,dave" \\
      --output-csv  access_report.csv \\
      --output-md   access_report.md

Required PAT scopes
-------------------
  • read:org   – list org members, teams, and team members
  • repo       – list repos and outside collaborators

Dependencies
------------
  pip install PyGithub
"""

import argparse
import csv
import os
import sys
from datetime import datetime, timezone
from typing import Dict, List, Set

try:
    from github import Github, GithubException
except ImportError:
    print("ERROR: PyGithub is not installed.  Run: pip install PyGithub", file=sys.stderr)
    sys.exit(1)


# ---------------------------------------------------------------------------
# Data helpers
# ---------------------------------------------------------------------------

def get_all_repos(org):
    """Return a list of all repositories in the organisation."""
    print(f"[*] Fetching repositories for organisation '{org.login}' …")
    repos = list(org.get_repos())
    print(f"    Found {len(repos)} repositories.")
    return repos


def get_org_members(org) -> Dict[str, str]:
    """
    Return {login: role} for every organisation member.
    Role is either 'owner' or 'member'.
    """
    print("[*] Fetching organisation members …")
    members: Dict[str, str] = {}
    for m in org.get_members(role="all"):
        # Determine whether the member is an owner
        role = "owner" if org.has_in_members(m) else "member"
        members[m.login.lower()] = role

    # A second pass to correctly label owners
    for owner in org.get_members(role="owner"):
        members[owner.login.lower()] = "owner"

    print(f"    Found {len(members)} organisation members.")
    return members


def get_teams_for_repo(repo) -> List[dict]:
    """
    Return a list of {team_name, team_slug, permission, members} dicts for
    all teams that have explicit access to *repo*.
    """
    team_entries = []
    try:
        for team in repo.get_teams():
            members = [m.login.lower() for m in team.get_members()]
            team_entries.append(
                {
                    "team_name": team.name,
                    "team_slug": team.slug,
                    "permission": team.permission,
                    "members": members,
                }
            )
    except GithubException as exc:
        print(f"    WARN: could not fetch teams for {repo.name}: {exc}", file=sys.stderr)
    return team_entries


def get_outside_collaborators(repo) -> List[dict]:
    """
    Return a list of {login, permission} dicts for all outside collaborators
    (users who were invited directly to the repo but are not org members).
    """
    collabs = []
    try:
        for collab in repo.get_collaborators(affiliation="outside"):
            perms = collab.permissions
            if perms.admin:
                perm_label = "admin"
            elif perms.push:
                perm_label = "write"
            elif perms.pull:
                perm_label = "read"
            else:
                perm_label = "unknown"
            collabs.append({"login": collab.login.lower(), "permission": perm_label})
    except GithubException as exc:
        print(
            f"    WARN: could not fetch outside collaborators for {repo.name}: {exc}",
            file=sys.stderr,
        )
    return collabs


# ---------------------------------------------------------------------------
# Core audit logic
# ---------------------------------------------------------------------------

def audit_organisation(
    token: str,
    org_name: str,
    users_to_check: List[str],
) -> List[dict]:
    """
    Perform the full audit and return a list of row dicts suitable for CSV/MD.
    """
    g = Github(token, per_page=100)

    try:
        org = g.get_organization(org_name)
    except GithubException as exc:
        print(f"ERROR: cannot access organisation '{org_name}': {exc}", file=sys.stderr)
        sys.exit(1)

    repos = get_all_repos(org)
    org_members = get_org_members(org)
    users_lower: Set[str] = {u.lower() for u in users_to_check}

    rows: List[dict] = []

    for idx, repo in enumerate(repos, start=1):
        print(f"  [{idx:3}/{len(repos)}] Auditing {repo.full_name} …")

        outside_collabs = get_outside_collaborators(repo)
        teams = get_teams_for_repo(repo)

        # Build a per-user access record for this repo
        user_access: Dict[str, dict] = {}

        # 1. Organisation members inherit base access (read for public repos;
        #    for private repos access depends on team membership or direct invite)
        for login, role in org_members.items():
            user_access.setdefault(login, {
                "login": login,
                "repo": repo.full_name,
                "repo_visibility": "private" if repo.private else "public",
                "via_org_membership": role,      # owner / member
                "via_teams": [],
                "via_direct_collab": None,
                "flagged": login in users_lower,
            })
            user_access[login]["via_org_membership"] = role

        # 2. Team memberships
        for team in teams:
            for member_login in team["members"]:
                user_access.setdefault(member_login, {
                    "login": member_login,
                    "repo": repo.full_name,
                    "repo_visibility": "private" if repo.private else "public",
                    "via_org_membership": org_members.get(member_login, "external"),
                    "via_teams": [],
                    "via_direct_collab": None,
                    "flagged": member_login in users_lower,
                })
                user_access[member_login]["via_teams"].append(
                    f"{team['team_name']} ({team['permission']})"
                )

        # 3. Outside collaborators (direct invites)
        for collab in outside_collabs:
            login = collab["login"]
            user_access.setdefault(login, {
                "login": login,
                "repo": repo.full_name,
                "repo_visibility": "private" if repo.private else "public",
                "via_org_membership": org_members.get(login, "non-member"),
                "via_teams": [],
                "via_direct_collab": None,
                "flagged": login in users_lower,
            })
            user_access[login]["via_direct_collab"] = collab["permission"]

        # Flatten and collect rows
        for login, rec in user_access.items():
            rows.append({
                "repository": rec["repo"],
                "visibility": rec["repo_visibility"],
                "username": rec["login"],
                "org_role": rec["via_org_membership"],
                "via_teams": "; ".join(rec["via_teams"]) if rec["via_teams"] else "",
                "via_direct_collab": rec["via_direct_collab"] or "",
                "flagged_user": "YES" if rec["flagged"] else "",
            })

    return rows


# ---------------------------------------------------------------------------
# Output helpers
# ---------------------------------------------------------------------------

CSV_FIELDS = [
    "repository",
    "visibility",
    "username",
    "org_role",
    "via_teams",
    "via_direct_collab",
    "flagged_user",
]


def write_csv(rows: List[dict], path: str) -> None:
    with open(path, "w", newline="", encoding="utf-8") as fh:
        writer = csv.DictWriter(fh, fieldnames=CSV_FIELDS)
        writer.writeheader()
        writer.writerows(rows)
    print(f"[+] CSV report written to: {path}")


def write_markdown(
    rows: List[dict],
    org_name: str,
    users_to_check: List[str],
    path: str,
) -> None:
    flagged_rows = [r for r in rows if r["flagged_user"] == "YES"]
    users_lower = {u.lower() for u in users_to_check}

    with open(path, "w", encoding="utf-8") as fh:
        ts = datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M UTC")
        fh.write(f"# GitHub Access Audit – `{org_name}`\n\n")
        fh.write(f"Generated: {ts}  \n")
        fh.write(f"Total rows: {len(rows)}  \n\n")

        # --- Flagged-user summary -----------------------------------------
        fh.write("## Flagged Users Summary\n\n")
        if not users_to_check:
            fh.write("_No users were flagged for checking._\n\n")
        else:
            fh.write(
                f"Checked for the following users: "
                + ", ".join(f"`{u}`" for u in users_to_check)
                + "\n\n"
            )

        if not flagged_rows:
            fh.write(
                "> ✅ **None of the flagged users appear in the access lists.**\n\n"
            )
        else:
            fh.write(
                f"> ⚠️  **{len(flagged_rows)} access record(s) found for flagged users.**\n\n"
            )
            fh.write(
                "| Repository | Visibility | Username | Org Role | Via Teams | Via Direct Collab |\n"
            )
            fh.write(
                "|------------|------------|----------|----------|-----------|-------------------|\n"
            )
            for r in sorted(flagged_rows, key=lambda x: (x["username"], x["repository"])):
                fh.write(
                    f"| {r['repository']} | {r['visibility']} | `{r['username']}` "
                    f"| {r['org_role']} | {r['via_teams'] or '–'} "
                    f"| {r['via_direct_collab'] or '–'} |\n"
                )
            fh.write("\n")

        # --- Per-user breakdown for flagged users -------------------------
        for user in users_to_check:
            u_lower = user.lower()
            u_rows = [r for r in flagged_rows if r["username"] == u_lower]
            fh.write(f"### User: `{user}`\n\n")
            if not u_rows:
                fh.write(f"> ✅ `{user}` has **no detected access** to any repository.\n\n")
            else:
                fh.write(
                    f"> ⚠️  `{user}` still has access to **{len(u_rows)}** "
                    f"repository/repositories:\n\n"
                )
                fh.write(
                    "| Repository | Visibility | Org Role | Via Teams | Via Direct Collab |\n"
                )
                fh.write(
                    "|------------|------------|----------|-----------|-------------------|\n"
                )
                for r in sorted(u_rows, key=lambda x: x["repository"]):
                    fh.write(
                        f"| {r['repository']} | {r['visibility']} "
                        f"| {r['org_role']} | {r['via_teams'] or '–'} "
                        f"| {r['via_direct_collab'] or '–'} |\n"
                    )
                fh.write("\n")

        # --- Full access table --------------------------------------------
        fh.write("## Full Access Table\n\n")
        fh.write(
            "| Repository | Visibility | Username | Org Role | Via Teams | Via Direct Collab | Flagged |\n"
        )
        fh.write(
            "|------------|------------|----------|----------|-----------|-------------------|--------|\n"
        )
        for r in rows:
            flag_icon = "⚠️" if r["flagged_user"] == "YES" else ""
            fh.write(
                f"| {r['repository']} | {r['visibility']} | `{r['username']}` "
                f"| {r['org_role']} | {r['via_teams'] or '–'} "
                f"| {r['via_direct_collab'] or '–'} | {flag_icon} |\n"
            )

    print(f"[+] Markdown report written to: {path}")


# ---------------------------------------------------------------------------
# CLI entry-point
# ---------------------------------------------------------------------------

def parse_args():
    parser = argparse.ArgumentParser(
        description="Audit repository access for every repo in a GitHub organisation.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=__doc__,
    )
    parser.add_argument(
        "--org",
        required=True,
        help="GitHub organisation name (e.g. yuyuanweb)",
    )
    parser.add_argument(
        "--token",
        default=os.environ.get("GITHUB_TOKEN"),
        help="GitHub Personal Access Token (default: $GITHUB_TOKEN env var)",
    )
    parser.add_argument(
        "--check-users",
        default="",
        help="Comma-separated list of GitHub usernames to flag (e.g. alice,bob)",
    )
    parser.add_argument(
        "--output-csv",
        default="access_report.csv",
        help="Path for the CSV output file (default: access_report.csv)",
    )
    parser.add_argument(
        "--output-md",
        default="access_report.md",
        help="Path for the Markdown output file (default: access_report.md)",
    )
    return parser.parse_args()


def main():
    args = parse_args()

    if not args.token:
        print(
            "ERROR: No GitHub token provided.  "
            "Set GITHUB_TOKEN or use --token.",
            file=sys.stderr,
        )
        sys.exit(1)

    users_to_check = [u.strip() for u in args.check_users.split(",") if u.strip()]

    print(f"[*] Starting access audit for organisation: {args.org}")
    if users_to_check:
        print(f"[*] Flagging users: {', '.join(users_to_check)}")

    rows = audit_organisation(args.token, args.org, users_to_check)

    write_csv(rows, args.output_csv)
    write_markdown(rows, args.org, users_to_check, args.output_md)

    print(f"\n[+] Audit complete. {len(rows)} access records collected.")

    # Exit with non-zero if any flagged user was found (useful for CI)
    flagged_count = sum(1 for r in rows if r["flagged_user"] == "YES")
    if flagged_count:
        print(
            f"[!] WARNING: {flagged_count} access record(s) belong to flagged users!",
            file=sys.stderr,
        )
        sys.exit(2)


if __name__ == "__main__":
    main()
