import { GraphEvents } from "react-vis-graph-wrapper";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Typography,
} from "@mui/material";

import GenericQuiver from "../common/GenericQuiver";

interface QuiverAccordionProps {
  header: string;
  description?: string;
  url: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  physicOption?: any;
  events?: GraphEvents;
}

export default function QuiverAccordion({
  header,
  description,
  url,
  physicOption = undefined,
  events,
}: QuiverAccordionProps) {
  return (
    <Accordion TransitionProps={{ unmountOnExit: true }}>
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <Typography fontWeight="medium">{header}</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Typography whiteSpace={"pre-wrap"}>{description}</Typography>
        <GenericQuiver
          url={url}
          physicOption={physicOption}
          events={events}
          allowChosen={true}
        />
      </AccordionDetails>
    </Accordion>
  );
}
