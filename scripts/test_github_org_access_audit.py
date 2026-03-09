"""
Unit tests for github_org_access_audit.py
Tests the report-writing helpers using mock data (no GitHub credentials needed).
"""

import csv
import io
import os
import sys
import tempfile
import unittest

# Add the scripts directory to the path so we can import the module
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from github_org_access_audit import write_csv, write_markdown, CSV_FIELDS

# ---------------------------------------------------------------------------
# Sample data
# ---------------------------------------------------------------------------

SAMPLE_ROWS = [
    {
        "repository": "yuyuanweb/repo-a",
        "visibility": "private",
        "username": "alice",
        "org_role": "member",
        "via_teams": "backend-team (write)",
        "via_direct_collab": "",
        "flagged_user": "YES",
    },
    {
        "repository": "yuyuanweb/repo-b",
        "visibility": "public",
        "username": "bob",
        "org_role": "owner",
        "via_teams": "",
        "via_direct_collab": "",
        "flagged_user": "YES",
    },
    {
        "repository": "yuyuanweb/repo-a",
        "visibility": "private",
        "username": "carol",
        "org_role": "member",
        "via_teams": "frontend-team (read)",
        "via_direct_collab": "write",
        "flagged_user": "",
    },
]


class TestWriteCsv(unittest.TestCase):
    def test_csv_has_header_and_all_rows(self):
        with tempfile.NamedTemporaryFile(
            mode="w", suffix=".csv", delete=False
        ) as tmp:
            tmp_path = tmp.name

        try:
            write_csv(SAMPLE_ROWS, tmp_path)
            with open(tmp_path, newline="", encoding="utf-8") as fh:
                reader = csv.DictReader(fh)
                rows = list(reader)

            self.assertEqual(len(rows), 3)
            self.assertEqual(set(rows[0].keys()), set(CSV_FIELDS))
            self.assertEqual(rows[0]["username"], "alice")
            self.assertEqual(rows[0]["flagged_user"], "YES")
            self.assertEqual(rows[2]["username"], "carol")
            self.assertEqual(rows[2]["flagged_user"], "")
        finally:
            os.unlink(tmp_path)

    def test_csv_fields_order(self):
        with tempfile.NamedTemporaryFile(
            mode="w", suffix=".csv", delete=False
        ) as tmp:
            tmp_path = tmp.name

        try:
            write_csv(SAMPLE_ROWS, tmp_path)
            with open(tmp_path, newline="", encoding="utf-8") as fh:
                reader = csv.reader(fh)
                header = next(reader)
            self.assertEqual(header, CSV_FIELDS)
        finally:
            os.unlink(tmp_path)


class TestWriteMarkdown(unittest.TestCase):
    def _generate(self, rows, users_to_check):
        with tempfile.NamedTemporaryFile(
            mode="w", suffix=".md", delete=False, encoding="utf-8"
        ) as tmp:
            tmp_path = tmp.name

        write_markdown(rows, "yuyuanweb", users_to_check, tmp_path)
        with open(tmp_path, encoding="utf-8") as fh:
            content = fh.read()
        os.unlink(tmp_path)
        return content

    def test_flagged_users_appear_in_summary(self):
        content = self._generate(SAMPLE_ROWS, ["alice", "bob"])
        self.assertIn("alice", content)
        self.assertIn("bob", content)
        self.assertIn("⚠️", content)

    def test_no_flagged_users_shows_ok_message(self):
        content = self._generate(SAMPLE_ROWS, ["nobody"])
        self.assertIn("✅", content)

    def test_empty_check_users(self):
        content = self._generate(SAMPLE_ROWS, [])
        self.assertIn("No users were flagged", content)

    def test_full_access_table_present(self):
        content = self._generate(SAMPLE_ROWS, [])
        self.assertIn("Full Access Table", content)
        self.assertIn("carol", content)

    def test_per_user_breakdown_for_each_flagged_user(self):
        content = self._generate(SAMPLE_ROWS, ["alice", "bob"])
        self.assertIn("### User: `alice`", content)
        self.assertIn("### User: `bob`", content)

    def test_non_flagged_user_gets_no_access_message(self):
        content = self._generate(SAMPLE_ROWS, ["nobody"])
        self.assertIn("no detected access", content)

    def test_org_name_in_title(self):
        content = self._generate(SAMPLE_ROWS, [])
        self.assertIn("yuyuanweb", content)


if __name__ == "__main__":
    unittest.main()
