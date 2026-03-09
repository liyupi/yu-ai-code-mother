# Scripts

This directory contains administrative and operational scripts for the project.

---

## `github_org_access_audit.py` — GitHub Organization Access Audit Tool

A security-audit script that enumerates **every access pathway** for every
repository in a GitHub organisation and generates both a CSV and a Markdown
report.  It is designed to quickly answer the question *"can a specific
GitHub user still reach any of our repositories?"* — for example, when
verifying that departed employees have been fully off-boarded.

### Access pathways checked per repository

| Pathway | What is captured |
|---------|-----------------|
| **Organisation membership** | Whether the user is an org `owner` or `member` |
| **Team membership** | Which teams grant the user access, and with what permission level (`read` / `write` / `admin`) |
| **Outside collaborator** | Whether the user was directly invited to the repo and with what permission level |

### Requirements

| Requirement | Notes |
|-------------|-------|
| Python ≥ 3.8 | Standard library + one third-party package |
| `PyGithub` | `pip install PyGithub` |
| GitHub PAT | Needs **`read:org`** + **`repo`** scopes (or a fine-grained token with equivalent permissions) |
| Org admin (recommended) | Listing outside collaborators on private repos requires admin access |

### Installation

```bash
pip install PyGithub
```

### Quick start

```bash
export GITHUB_TOKEN="ghp_xxxxxxxxxxxxxxxxxxxx"

python3 scripts/github_org_access_audit.py \
    --org yuyuanweb \
    --check-users "alice,bob,charlie,dave" \
    --output-csv  access_report.csv \
    --output-md   access_report.md
```

Replace `alice,bob,charlie,dave` with the actual GitHub usernames of the
former employees (or any users) you need to verify.

### All options

```
usage: github_org_access_audit.py [-h] --org ORG [--token TOKEN]
                                  [--check-users CHECK_USERS]
                                  [--output-csv OUTPUT_CSV]
                                  [--output-md OUTPUT_MD]

options:
  --org ORG             GitHub organisation name (e.g. yuyuanweb)
  --token TOKEN         GitHub PAT (default: $GITHUB_TOKEN env var)
  --check-users USERS   Comma-separated GitHub usernames to flag
  --output-csv PATH     CSV output path  (default: access_report.csv)
  --output-md  PATH     Markdown output path (default: access_report.md)
```

### Output files

#### `access_report.csv`

One row per (repository, user) pair.  Columns:

| Column | Description |
|--------|-------------|
| `repository` | `org/repo-name` |
| `visibility` | `public` or `private` |
| `username` | GitHub login (lowercase) |
| `org_role` | `owner`, `member`, or `non-member` / `external` |
| `via_teams` | Semicolon-separated list of teams and permissions |
| `via_direct_collab` | Direct-collab permission (`read` / `write` / `admin`) or blank |
| `flagged_user` | `YES` if the username was passed via `--check-users` |

#### `access_report.md`

Human-readable Markdown with three sections:

1. **Flagged Users Summary** – a concise table showing only the rows that
   belong to the users supplied via `--check-users`.
2. **Per-user breakdown** – one subsection per flagged user listing every
   repository they can reach and why.
3. **Full Access Table** – the complete (repository × user) matrix with
   flagged rows marked with ⚠️.

### Exit codes

| Code | Meaning |
|------|---------|
| `0` | Audit completed, no flagged users found in any repo |
| `1` | Fatal error (bad token, org not found, missing dependency, etc.) |
| `2` | Audit completed, but ≥ 1 flagged user was found (useful for CI gating) |

### Example: off-boarding checklist

After running the script, if the Markdown report shows any flagged user with
a `⚠️` icon:

1. Remove the user from any listed **teams**.
2. Remove any **direct collaborator** invitations.
3. If the user is still shown as an **org member** (`owner` / `member`), remove
   them from the organisation entirely in *GitHub → Organisation settings →
   Members*.
4. Re-run the audit to confirm they no longer appear.

### Security note

The output files may contain sensitive personnel information.  Do **not**
commit `access_report.csv` or `access_report.md` to the repository.
Consider adding them to `.gitignore`:

```
access_report.csv
access_report.md
```
