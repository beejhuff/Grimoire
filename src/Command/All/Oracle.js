// @flow

import BaseCommand from '../BaseCommand';
import Commons from '../../Utils/Commons';

export default class Prints extends BaseCommand {

  constructor(commons: Commons) {
    super(
      commons,
      'oracle',
      '[card name]',
      'Retrieves the oracle text of a card. If no card is specified, it will take the last mentioned card.',
      ['cardtext']
    );
    // Bind method(s)
    this.exec = this.exec.bind(this);
  }

  exec: (args: Array<string>, userId: string, channelId: string, guildId?: ?string) => Promise<void>;
  async exec(args: Array<string>, userId: string, channelId: string, guildId?: ?string) {
    let card;
    try {
      card = await this.commons.obtainRecentOrSpecifiedCard(args.join(' '), channelId);
    } catch (err) {
      this.commons.sendMessage(`<@${userId}>, ${err}`, userId, channelId);
      return;
    }

    // Let the user know if there are no known rulings
    if (!card.originalText) {
      this.commons.sendMessage(`<@${userId}>, There is no known oracle text for **'${card.name}'**`, userId, channelId);
      return;
    }

    const emojiMap = {
      'W': 'manaW', // eslint-disable-line
      'U': 'manaU', // eslint-disable-line
      'B': 'manaB', // eslint-disable-line
      'R': 'manaR', // eslint-disable-line
      'G': 'manaG', // eslint-disable-line
      'C': 'manaC', // eslint-disable-line
      'W/U': 'manaWU', // eslint-disable-line
      'U/B': 'manaUB', // eslint-disable-line
      'B/R': 'manaBR', // eslint-disable-line
      'R/G': 'manaRG', // eslint-disable-line
      'G/W': 'manaGW', // eslint-disable-line
      'W/B': 'manaWB', // eslint-disable-line
      'U/R': 'manaUR', // eslint-disable-line
      'B/G': 'manaBG', // eslint-disable-line
      'R/W': 'manaRW', // eslint-disable-line
      'G/U': 'manaGU', // eslint-disable-line
      '2/W': 'mana2W', // eslint-disable-line
      '2/U': 'mana2U', // eslint-disable-line
      '2/B': 'mana2B', // eslint-disable-line
      '2/R': 'mana2R', // eslint-disable-line
      '2/G': 'mana2G', // eslint-disable-line
      'WP': 'manaWP', // eslint-disable-line
      'UP': 'manaUP', // eslint-disable-line
      'BP': 'manaBP', // eslint-disable-line
      'RP': 'manaRP', // eslint-disable-line
      'GP': 'manaGP', // eslint-disable-line
      '0': 'manaZero', // eslint-disable-line
      '1': 'manaOne', // eslint-disable-line
      '2': 'manaTwo', // eslint-disable-line
      '3': 'manaThree', // eslint-disable-line
      '4': 'manaFour', // eslint-disable-line
      '5': 'manaFive', // eslint-disable-line
      '6': 'manaSix', // eslint-disable-line
      '7': 'manaSeven', // eslint-disable-line
      '8': 'manaEight', // eslint-disable-line
      '9': 'manaNine', // eslint-disable-line
      '10': 'manaTen', // eslint-disable-line
      '11': 'manaEleven', // eslint-disable-line
      '12': 'manaTwelve', // eslint-disable-line
      '13': 'manaThirteen', // eslint-disable-line
      '14': 'manaFourteen', // eslint-disable-line
      '15': 'manaFifteen', // eslint-disable-line
      '16': 'manaSixteen', // eslint-disable-line
      '20': 'manaTwenty', // eslint-disable-line
      'T': 'manaT', // eslint-disable-line
      'Q': 'manaQ', // eslint-disable-line
      'S': 'manaS', // eslint-disable-line
      'X': 'manaX', // eslint-disable-line
      'E': 'manaE' // eslint-disable-line
    };

    // Replace symols
    let text = card.originalText;
    if (guildId) {
      Object.keys(emojiMap).forEach(v => {
        const emoji = this.commons.getEmoji(emojiMap[v], guildId || '');
        if (!emoji) return;
        text = text.replace(new RegExp(`\\{${v}\\}`, 'g'), emoji.toString());
      });
    }

    // Construct message
    const message: string = `<@${userId}>, Here is the oracle text for **'${card.name}'**:\n\n${text}`;

    // Send message
    this.commons.sendMessage(message, userId, channelId);
  }

}