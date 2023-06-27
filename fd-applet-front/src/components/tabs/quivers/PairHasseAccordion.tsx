import { useState } from "react";

import { GraphEvents } from "react-vis-graph-wrapper";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Typography,
} from "@mui/material";

import { Combo } from "../../common/Combo";
import GenericQuiver from "../../common/GenericQuiver";
import { pairOptions } from "../enumerator/PairEnumerator";

import { HassePhysicsOption } from "./QuiversTab";

interface PairHasseAccordionProps {
  events: GraphEvents;
}

export default function PairHasseAccordion({
  events,
}: PairHasseAccordionProps) {
  const [selectedMenu, setSelectedMenu] = useState("tors");

  return (
    <Accordion TransitionProps={{ unmountOnExit: true }}>
      <AccordionSummary expandIcon={<ExpandMoreIcon />} sx={{ m: 0 }}>
        <Typography fontWeight="medium">Hasse quiver of pair of subcategories</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Typography mb={2}>
          Hasse quiver of inclusion with respect to 1st subcategories.
        </Typography>

        <Box mb={2}>
          <Combo
            title="Choose types"
            options={pairOptions}
            selected={selectedMenu}
            setSelected={setSelectedMenu}
          />
        </Box>
        <GenericQuiver
          showDuration={true}
          url={"/api/quiver/pair/" + selectedMenu}
          physicOption={HassePhysicsOption}
          updateButton
          allowChosen
          events={events}
        />

      </AccordionDetails>
    </Accordion>
  );
}
