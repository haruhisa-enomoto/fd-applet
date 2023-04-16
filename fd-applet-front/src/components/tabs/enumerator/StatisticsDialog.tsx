import { useState } from "react";

import EqualizerIcon from "@mui/icons-material/Equalizer";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";

import { Pair, PairTable } from "../../common/PairTable";

interface StatisticsDialogProps {
  data: string[][];
}

export default function StatisticsDialog({ data }: StatisticsDialogProps) {
  const [open, setOpen] = useState(false);
  const [statistics, setStatistics] = useState<Pair<number, number>[]>();

  const handleClose = () => {
    setOpen(false);
  };

  const handleClick = () => {
    setOpen(true);
    const counts = data.reduce((obj: Record<number, number>, arr) => {
      const len = arr.length;
      obj[len] = (obj[len] ?? 0) + 1;
      return obj;
    }, {});

    const result: Pair<number, number>[] = Object.entries(counts).map(
      ([key, value]) => ({
        first: parseInt(key),
        second: value,
      })
    );
    setStatistics(result);
  };

  return (
    <>
      <Button onClick={handleClick} startIcon={<EqualizerIcon />}>
        Show Distribution
      </Button>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Distribution of the number of indecomposables</DialogTitle>
        <DialogContent>
          <DialogContentText>
            This table displays the counts of objects with given number of
            indecomposables in the list.
          </DialogContentText>
          {statistics && (
            <PairTable
              firstName="Numbers of indecs"
              secondName="Numbers of objects"
              data={statistics}
            />
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Close</Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
