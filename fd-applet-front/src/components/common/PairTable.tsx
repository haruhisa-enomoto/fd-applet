import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from "@mui/material";

export type Pair<T, U> = { first: T; second: U };

interface PairTableProps {
  firstName: string;
  secondName: string;
  data: Pair<string | number | null, string | number | null>[];
}

export function PairTable({ firstName, secondName, data }: PairTableProps) {
  return (
    <TableContainer
      component={Paper}
      sx={{ display: "flex", m: 2, width: "80%", mx: "auto" }}
    >
      <Table
        // sx={{ width: "50%" }}
        aria-label="data table"
        size="small"
      >
        <TableHead>
          <TableRow>
            <TableCell width="60%">{firstName}</TableCell>
            <TableCell width="40%">{secondName}</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {data.map(
            (row) =>
              row.second !== undefined && (
                <TableRow key={row.first}>
                  <TableCell component="th" scope="row">
                    {row.first}
                  </TableCell>
                  <TableCell>
                    {row.second !== null ? row.second : "infinity"}
                  </TableCell>
                </TableRow>
              )
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
