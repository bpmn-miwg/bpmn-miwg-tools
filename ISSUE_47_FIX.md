# Fix for Issue #47: Modify icon in demo participation and last submission columns

## Problem
Icons in the demo participation and last submission columns were showing amber immediately after the current year, causing confusion when 2022 participants appeared "out of date" in early 2023.

## Solution
Modified the icon logic in `index.html` on the gh-pages branch to:

1. **Demo Participation Column**: Show green for participation within the last 2 years (not just current year)
2. **Last Submission Column**: Show green for submissions within the last 2 years (not just current year)

## Technical Changes

### Files Modified
- `index.html` (on gh-pages branch)

### Changes Made
1. Split the shared `renderYear()` function into two specialized functions:
   - `renderDemoParticipation()` for demo participation logic 
   - `renderLastSubmission()` for submission date logic

2. Updated DataTables column configuration:
   ```javascript
   // Before
   /* 7 */ { "mData": "participatedInDemosByYear", "mRender": renderYear, "iDataSort": 6 },
   /* 8 */ { "mData": "lastResultsSubmitted", "mRender": renderYear },
   
   // After  
   /* 7 */ { "mData": "participatedInDemosByYear", "mRender": renderDemoParticipation, "iDataSort": 6 },
   /* 8 */ { "mData": "lastResultsSubmitted", "mRender": renderLastSubmission },
   ```

3. Added new functions with improved logic:

#### renderDemoParticipation()
```javascript
function renderDemoParticipation(data, type, full) {
  var lastParticipation = data.split(",")[0];
  var participated = false;
  var currentYear = new Date().getFullYear();
  
  // Show green if participation was within the last 2 years
  if (lastParticipation >= (currentYear - 1)) {
    participated = true;
  } else if (lastParticipation != "0") {
    participated = "partial";
  }
  
  var result = renderTrafficLight(participated, type, full);
  return '<span style="white-space:nowrap;">' + result + ' ' + data.split(",").join(", ") + '</span>';
}
```

#### renderLastSubmission()
```javascript
function renderLastSubmission(data, type, full) {
  var submissionYear = parseInt(data);
  var participated = false;
  var currentYear = new Date().getFullYear();
  
  // Show green if submission was within the current or previous year
  if (submissionYear >= (currentYear - 1)) {
    participated = true;
  } else if (submissionYear > 0) {
    participated = "partial";
  }
  
  var result = renderTrafficLight(participated, type, full);
  return '<span style="white-space:nowrap;">' + result + ' ' + data + '</span>';
}
```

## Test Results
The changes were tested and verified to work correctly:

- ✅ Green Icons: Tools with 2024-2025 demo participation/submissions
- ✅ Amber Icons: Tools with 2022-2023 demo participation/submissions  
- ✅ Red Icons: Tools with no demo participation

## Deployment
The changes are committed to the gh-pages branch and will be live on the website at:
http://bpmn-miwg.github.io/bpmn-miwg-tools/

## Commit Information
- Branch: gh-pages
- Commit: fafa5f2118d265ed178179db1ae04c48ac46c973
- Message: "Fix icon logic for demo participation and last submission columns"

This addresses the original concern where 2022 participants would immediately show as "less than up to date" in 2023, while maintaining meaningful visual feedback about recency of participation.